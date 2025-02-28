/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.service.impl.QuoteServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.workstream.entity.QuoteProposal;
import com.maan.eway.workstream.entity.QuoteProposalPK;
import com.maan.eway.workstream.entity.WorkflowFactorRateRequestDetail;
import com.maan.eway.workstream.entity.WorkflowTracking;
import com.maan.eway.workstream.repository.QuoteProposalRepository;
import com.maan.eway.workstream.request.ApproverGetReq;
import com.maan.eway.workstream.request.QuoteProposalActionReq;
import com.maan.eway.workstream.request.QuoteProposalSaveReq;
import com.maan.eway.workstream.response.ApproverRes;
import com.maan.eway.workstream.response.QuoteProposalRes;
import com.maan.eway.workstream.response.WorkflowTrackingRes;
import com.maan.eway.workstream.service.QuoteProposalService;
import com.maan.eway.workstream.service.WorkflowTrackingService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;

@Service
public class QuoteProposalServiceImpl implements QuoteProposalService {	
	private static final Logger log = LogManager.getLogger(QuoteProposalServiceImpl.class);
	
	private static final String ACTION_ASSIGNED = "AS";
	private static final String ACTION_APPROVED = "AP";
	private static final String ACTION_ESCALATED = "ES";
	private static final String ACTION_REJECTED = "RJ";
	private static final String ACTION_REVOKED = "RV";	
	
	private static final String NEW_QUOTE = "NEWQUOTE";
	
	private static final String STATUS_PROCESSING = "PR";
	private static final String STATUS_ACCEPTED = "AC";
	private static final String STATUS_DECLINED = "DC";
	
	private static final String REFERRAL_APPROVED = "RA";
	private static final String REFERRAL_REJECTED = "RR";
	
	private QuoteServiceImpl quoteService;
	private QuoteProposalRepository proposalRepo;
	private ApproverServiceImpl approverService;
	private WorkflowTrackingServiceImpl workflowService;
	private HierarchyManagementServiceImpl hierarchyService;
	private WorkflowFactorRateRequestDetailServiceImpl workflowFactorService; 
	private EntityManager entityManager;
	private ModelMapper mapper;

	@Autowired
	public QuoteProposalServiceImpl(QuoteServiceImpl quoteService, QuoteProposalRepository proposalRepo,
			ApproverServiceImpl approverService, WorkflowTrackingServiceImpl workflowService,
			HierarchyManagementServiceImpl hierarchyService,
			WorkflowFactorRateRequestDetailServiceImpl workflowFactorService, EntityManager entityManager,
			ModelMapper mapper) {
		this.quoteService = quoteService;
		this.proposalRepo = proposalRepo;
		this.approverService = approverService;
		this.workflowService = workflowService;
		this.hierarchyService = hierarchyService;
		this.workflowFactorService = workflowFactorService;
		this.entityManager = entityManager;
		this.mapper = mapper;
	}


	public List<Error> validateParametersOfApproverGetReq(ApproverGetReq req) {
		List<Error> errors = new ArrayList<>();
		if(req.getCompanyId() == null) {
			errors.add(new Error("11", "CompanyId", "CompanyId Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("12", "ProductId", "ProductId Should Not Be Null"));
		}		
		if(req.getLoginId() == null || req.getLoginId().isBlank()) {
			errors.add(new Error("13", "LoginId", "LoginId Should Not Be Blank"));
		}
		return errors;		
	}



	public List<Error> validateParametersOfProposalActionReq(QuoteProposalActionReq req) {
		List<Error> errors = new ArrayList<>();
		if(req.getCompanyId() == null) {
			errors.add(new Error("11", "CompanyId", "CompanyId Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("12", "ProductId", "ProductId Should Not Be Null"));
		}
		if(req.getProposalId() == null) {
			errors.add(new Error("13", "ProposalId", "ProposalId Should Not Be Null"));
		}
		if(req.getLoginId() == null || req.getLoginId().isBlank()) {
			errors.add(new Error("14", "LoginId", "LoginId Should Not Be Blank"));
		}
		if(req.getActionTaken() == null) {
			errors.add(new Error("15", "ActionTaken", "ActionTaken Should Not Be Null"));
		}
		if( !(req.getActionTaken().equals(ACTION_ASSIGNED) || req.getActionTaken().equals(ACTION_APPROVED) ||
				req.getActionTaken().equals(ACTION_REJECTED) || req.getActionTaken().equals(ACTION_REVOKED) ||
				req.getActionTaken().equals(ACTION_ESCALATED) )) {
			errors.add(new Error("16", "ActionTaken", "This is not a valid Action"));
		}
		if(!req.getActionTaken().equals(ACTION_ASSIGNED)) {
			if(req.getTotalPremium() == null) {
				errors.add(new Error("17", "TotalPremium", "TotalPremium Should Not Be Null"));
			}
			if(req.getCommissionModifyYn() == null || req.getCommissionModifyYn().isBlank()) {
				errors.add(new Error("18", "CommissionModifyYN", "CommissionModifyYN Should Not Be Blank"));
			}
			if(req.getCommissionPercent() == null) {
				errors.add(new Error("19", "CommissionPercent", "CommissionPercent Should Not Be Null"));
			}
		}

		return errors;		
	}

	
	/**
	 * Retrieves a list of all incoming proposals for a specific approver based on the approver's hierarchy level.
	 * 
	 * <p>This method checks the approver's hierarchy level and fetches proposals accordingly. It handles different 
	 * levels of hierarchy such as entry, top, and intermediate levels. For each level, it retrieves proposals marked as 
	 * "new" and "revoked" and sets the respective action times based on workflow events.</p>
	 * 
	 * <p>The proposals are sorted by the action taken on them, with the most recent actions appearing first.</p>
	 * 
	 * @param req the request containing the approver's details (company ID, product ID, and login ID).
	 * @return a list of {@link QuoteProposalRes} objects representing the incoming proposals for the approver, 
	 *         or {@code null} if an error occurs or no proposals are found.
	 */
	public List<QuoteProposalRes> allIncomingProposals(ApproverGetReq req) {
		try {
			Optional<ApproverRes> optApprover = approverService.getApprover(
					req.getCompanyId(), req.getProductId(), req.getLoginId());
			
			if(optApprover.isEmpty()) {
	            throw new NoSuchElementException("Approver not found for the provided details.");
			}
			
			ApproverRes approver = optApprover.get();
			Integer hisHierarchyValue = approver.getHierarchyValue();

			//Retrive all level for product			
			List<Integer> allHierarchyLevels = hierarchyService.retrieveAllLevelsForProduct(
					req.getCompanyId(), req.getProductId());			
			
			
			if(allHierarchyLevels.isEmpty()) {
				throw new NoSuchElementException("HierarchyLevel is not available");
			}
			
			if(!allHierarchyLevels.contains(hisHierarchyValue)) {
				throw new IllegalArgumentException("Approver Hierarchy Level is Not matched");
			}
			
			
			//For Entry Level	
			if(hisHierarchyValue.equals(allHierarchyLevels.get(0))) {
				List<QuoteProposal> newProposals = findIncomingProposalsForEntryLevel(approver.getCompanyId(),
						approver.getProductId(), approver.getSumInsuredStart(), approver.getSumInsuredEnd());
				
				List<QuoteProposalRes> newProposalsList = newProposals.stream()
						.map(proposal -> mapper.map(proposal, QuoteProposalRes.class))
						.collect(Collectors.toList());
				
				//Setting Action Time (Incoming Time of Each Proposal)
				for(QuoteProposalRes proposal : newProposalsList) {
					proposal.setActionTakenOn(proposal.getCreatedOn());
				}
				
				List<QuoteProposal> revokedProposals = revokedProposalBecomingIncoming(req.getCompanyId(), 
						req.getProductId(), hisHierarchyValue, approver.getSumInsuredStart(), approver.getSumInsuredEnd());
				
				List<QuoteProposalRes> revokedProposalsList = revokedProposals.stream()
						.map(proposal -> mapper.map(proposal, QuoteProposalRes.class))
						.collect(Collectors.toList());
				
				//Setting Action Time (Incoming Time of Each Proposal Marked With Revoked
				Map<Long, LocalDateTime> map = workflowService.findActionTimeOfRevokedWorkflowBecomeIncoming(
						approver.getCompanyId(), approver.getProductId(), hisHierarchyValue);
				if(map !=null) {
					for(QuoteProposalRes proposal : revokedProposalsList) {
						proposal.setActionTakenOn(map.get(proposal.getProposalId()));
					}
				}					
				
				return Stream.concat(newProposalsList.stream(), revokedProposalsList.stream())
							.sorted(Comparator.comparing(QuoteProposalRes::getActionTakenOn).reversed())
							.toList();
			}
			
			//for Top Level
			if(hisHierarchyValue.equals(allHierarchyLevels.get(allHierarchyLevels.size()-1))){
				List<QuoteProposal> newProposals = findingIncomingProposalsForTopLevel(
						approver.getCompanyId(), approver.getProductId());
				
				List<QuoteProposalRes> newProposalsList = newProposals.stream()
						.map(proposal -> mapper.map(proposal, QuoteProposalRes.class))
						.collect(Collectors.toList());
				
				//Setting Action Time (Incoming Time of Each Proposal marked with escalated)
				Map<Long, LocalDateTime> incomingMap = workflowService.findActionTimeOfEscalatedWorkflowBecomeIncoming(
						approver.getCompanyId(), approver.getProductId());
				if(incomingMap != null) {
					for(QuoteProposalRes proposal : newProposalsList) {
						proposal.setActionTakenOn(incomingMap.get(proposal.getProposalId()));
					}
				}
				
				
				List<QuoteProposal> revokedProposals = revokedProposalBecomingIncoming(approver.getCompanyId(), 
						approver.getProductId(), hisHierarchyValue, approver.getSumInsuredStart(), approver.getSumInsuredEnd());
				
				List<QuoteProposalRes> revokedProposalsList = revokedProposals.stream()
						.map(proposal -> mapper.map(proposal, QuoteProposalRes.class))
						.collect(Collectors.toList());
				
				//Setting Action Time (Incoming Time of Each Proposal Marked With Revoked)
				Map<Long, LocalDateTime> revokedMap = workflowService.findActionTimeOfRevokedWorkflowBecomeIncoming(
						approver.getCompanyId(), approver.getProductId(), hisHierarchyValue);
				if(revokedMap !=null) {
					for(QuoteProposalRes proposal : revokedProposalsList) {
						proposal.setActionTakenOn(revokedMap.get(proposal.getProposalId()));
					}
				}
								
				return Stream.concat(newProposalsList.stream(), revokedProposalsList.stream())
							.sorted(Comparator.comparing(QuoteProposalRes::getActionTakenOn).reversed())
							.toList();
			}
			
			//Any Intermediate Level
			else {
				int hisHierarchyIndex = allHierarchyLevels.indexOf(hisHierarchyValue);
				Integer prevHierarchyValue = allHierarchyLevels.get(hisHierarchyIndex -1);
				
				List<QuoteProposal> newProposals = findIncomingProposalsForRemainingLevels(approver.getCompanyId(), 
						approver.getProductId(), prevHierarchyValue, approver.getSumInsuredStart(), approver.getSumInsuredEnd());
				
				List<QuoteProposalRes> newProposalsList = newProposals.stream()
						.map(proposal -> mapper.map(proposal, QuoteProposalRes.class))
						.collect(Collectors.toList());
				
				//Setting Action Time (Incoming Time of Each Proposal marked approved by prev level)
				Map<Long, LocalDateTime> incomingMap = workflowService.findActionTimeOfApprovedWorkflowBecomeIncoming(
						approver.getCompanyId(), approver.getProductId(), hisHierarchyValue);
				if(incomingMap != null) {
					for(QuoteProposalRes proposal : newProposalsList) {
						proposal.setActionTakenOn(incomingMap.get(proposal.getProposalId()));
					}
				}
				
				
				List<QuoteProposal> revokedProposals = revokedProposalBecomingIncoming(req.getCompanyId(), 
						req.getProductId(), hisHierarchyValue, approver.getSumInsuredStart(), approver.getSumInsuredEnd());
				
				List<QuoteProposalRes> revokedProposalsList = revokedProposals.stream()
						.map(proposal -> mapper.map(proposal, QuoteProposalRes.class))
						.collect(Collectors.toList());
				
				//Setting Action Time (Incoming Time of Each Proposal Marked With Revoked
				Map<Long, LocalDateTime> revokedMap = workflowService.findActionTimeOfRevokedWorkflowBecomeIncoming(
						approver.getCompanyId(), approver.getProductId(), hisHierarchyValue);
				if(revokedMap !=null) {
					for(QuoteProposalRes proposal : revokedProposalsList) {
						proposal.setActionTakenOn(revokedMap.get(proposal.getProposalId() ));
					}
				}
			
				return Stream.concat(newProposalsList.stream(), revokedProposalsList.stream())
						.sorted(Comparator.comparing(QuoteProposalRes::getActionTakenOn).reversed())
						.toList();				
			}	
		} catch (Exception e) {
			log.error("Exception occurred: {}", e.getMessage(), e);			
			return null;
		}	
	}

	
	/**
	 * Retrieves a list of proposals that have been assigned to the approver.
	 * <p>
	 * This method fetches proposals that are assigned to the approver based on the company ID, product ID, 
	 * and login ID provided in the request. It then maps the proposals to {@link QuoteProposalRes} objects and sets 
	 * the action time for each proposal by fetching it from the workflow service.
	 * </p>
	 *
	 * @param req the request containing the company ID, product ID, and login ID of the approver.
	 * @return a list of {@link QuoteProposalRes} representing the assigned proposals for the approver, or {@code null} if an error occurs.
	 * @throws RuntimeException if there is an issue with retrieving the assigned proposals or setting the action time.
	 * @see QuoteProposalService#findingAssignedProposalsForEachApprover(Integer, Integer, String)
	 * @see WorkflowTrackingService#findActionTimeOfNonIncoming(Integer, Integer, String, String)
	 */
	public List<QuoteProposalRes> assignedForEach(ApproverGetReq req) {
		try {
			List<QuoteProposal> assignedForEach = findingAssignedProposalsForEachApprover(req.getCompanyId(), 
					req.getProductId(), req.getLoginId());
			
			List<QuoteProposalRes> assignedList = assignedForEach.stream()
						.map(pro -> mapper.map(pro, QuoteProposalRes.class))
						.toList();
			
			//setting action taken time from workflow
			Map<Long, LocalDateTime> map = workflowService.findActionTimeOfNonIncoming(
					req.getCompanyId(), req.getProductId(), req.getLoginId(), ACTION_ASSIGNED);
			if(map != null) {
				for(QuoteProposalRes proposal : assignedList) {
					proposal.setActionTakenOn(map.get(proposal.getProposalId() ));
				}
			}
			
			return assignedList.stream()
					.sorted(Comparator.comparing(QuoteProposalRes::getActionTakenOn).reversed())
					.toList();			
		} catch (Exception e) {
			log.error("Exception occurred: {}", e.getMessage(), e);
			return null;
		}
	}
	
	
	/**
	 * Retrieves a list of proposals for a specific action taken by each approver, excluding the assigned action.
	 * APPROVED, REJECTED, ESCALATED, REVOKED
	 * <p>
	 * This method fetches proposals for a given company ID, product ID, login ID, and action type (excluding assigned actions). 
	 * The proposals are sorted in descending order by proposal ID. The action taken time for each proposal is then set 
	 * by retrieving it from the workflow service.
	 * </p>
	 *
	 * @param companyId the ID of the company for which proposals are being fetched. 
	 * @param productId the ID of the product for which proposals are being fetched. 
	 * @param loginId the login ID of the approver. 
	 * @param action the action type to filter the proposals by (e.g., approved, rejected, etc.).
	 * @return a list of {@link QuoteProposalRes} representing proposals for the given action taken, or {@code null} if an error occurs.
	 * @throws RuntimeException if there is an issue with retrieving the proposals or setting the action taken time.
	 * @see QuoteProposalService#findingProposalsForEachApproverByActionTaken(Integer, Integer, String, String)
	 * @see WorkflowTrackingService#findActionTimeOfNonIncoming(Integer, Integer, String, String)
	 */	
	public List<QuoteProposalRes> actionTakenByEachExceptAssigned(
			Integer companyId, Integer productId, String loginId, String action) {
		try {
			List<QuoteProposal> quoteProposals = findingProposalsForEachApproverByActionTaken(
					companyId, productId, loginId, action);
			
			 List<QuoteProposalRes> proposalList = quoteProposals.stream()
					.map(pro -> mapper.map(pro, QuoteProposalRes.class))
					.toList();	
			
			 //setting action taken time from workflow
			Map<Long, LocalDateTime> map = workflowService.findActionTimeOfNonIncoming(companyId, productId, loginId, action);
			if(map != null) {
				for(QuoteProposalRes proposal : proposalList) {
					proposal.setActionTakenOn(
							map.get(proposal.getProposalId())
							);
				}
			}
			
			return proposalList.stream()
					.sorted(Comparator.comparing(QuoteProposalRes::getActionTakenOn).reversed())
					.toList();
		} catch (Exception e) {
			log.error("Exception occurred: {}", e.getMessage(), e);
			return null;
		}
	}
		
	
	/**
	 * Verifies whether the given proposal is incoming for the specified approver.
	 * The method checks if the proposal with the given proposal ID exists in the list 
	 * of incoming proposals for the approver, based on the company ID, product ID, and login ID.
	 * 
	 * <p>This method retrieves a list of incoming proposals for the specified approver and 
	 * checks if the proposal with the given ID is part of that list. It returns {@code true} 
	 * if the proposal is found in the list, otherwise {@code false}.</p>
	 * 
	 * @param req the request containing details about the proposal, including company ID, product ID, 
	 *            login ID of the approver, and the proposal ID to verify.
	 * @return {@code true} if the proposal is found in the list of incoming proposals for the approver, 
	 *         otherwise {@code false}.
	 */
	public boolean verifyTheProposalIsIncomingForApprover(QuoteProposalActionReq req) {
		ApproverGetReq approverGetReq = new ApproverGetReq(req.getCompanyId(), req.getProductId(), req.getLoginId());
		List<QuoteProposalRes> incomingForEachLevels = allIncomingProposals(approverGetReq);
		return incomingForEachLevels.stream()
				.anyMatch(pro -> pro.getProposalId().equals(req.getProposalId()));
	}

	
	/**
	 * Verifies if the given proposal has been assigned to the specified user (loginId).
	 * The method checks the latest workflow update for the proposal to determine if the 
	 * action "ASSIGNED" has been taken and whether it was assigned to the user identified by 
	 * the provided login ID.
	 * 
	 * <p>This method retrieves the latest workflow update associated with the proposal, 
	 * compares the login ID of the user who performed the "ASSIGNED" action, and returns 
	 * {@code true} if the proposal was assigned to the user. Otherwise, it returns {@code false}.</p>
	 * 
	 * @param companyId the ID of the company associated with the proposal.
	 * @param productId the ID of the product associated with the proposal.
	 * @param proposalId the ID of the proposal to check.
	 * @param loginId the login ID of the user to verify.
	 * @return {@code true} if the proposal was assigned to the specified user, otherwise {@code false}.
	 */
	public boolean verifyProposalAssignedToHimSelf(Integer companyId, 
			Integer productId, Long proposalId, String loginId) {
				
		WorkflowTrackingRes latestWorkflow = workflowService.latestUpdateOnProposal(
				companyId, productId, proposalId);
		if(latestWorkflow != null && 
			loginId.equals(latestWorkflow.getLoginId()) && 
			ACTION_ASSIGNED.equals(latestWorkflow.getActionTaken())) {
			return true;
		}		
		return false;		
	}
		
	
	/**
	 * Records an action on a proposal and updates its status and workflow accordingly. 
	 * This method processes various actions on the proposal, such as "Assigned", "Revoked", 
	 * "Approved", "Rejected", and "Escalated". It also handles the creation of workflow entries, 
	 * updates the last action on the proposal, and ensures that the necessary proposal statuses are set.
	 * 
	 * <p>This method performs the following steps:</p>
	 * <ol>
	 *     <li>For an "Assigned" action: creates a new workflow entry and updates the last action.</li>
	 *     <li>For a "Revoked" action: creates a new workflow entry and updates the last action.</li>
	 *     <li>For an "Approved" action: verifies the user's authority to finalize the proposal, 
	 *     sets the proposal status to "Accepted", updates the referral status, and creates a workflow entry.</li>
	 *     <li>For a "Rejected" action: sets the proposal status to "Declined", updates the referral 
	 *     status, and creates a workflow entry.</li>
	 *     <li>For an "Escalated" action: sets the proposal status to "Escalated" and updates the last action.</li>
	 * </ol>
	 * 
	 * <p>If any of the above actions fail (e.g., unable to create workflow entries, update statuses, 
	 * or set last action), the method throws a {@link RuntimeException} which triggers a rollback 
	 * due to the {@link Transactional} annotation. The method logs the error message in case of failure.</p>
	 *
	 * @param req the {@link QuoteProposalActionReq} object containing details for the proposal action, 
	 *            including company ID, product ID, proposal ID, action taken, and additional remarks.
	 * @return {@code true} if the action was successfully processed, otherwise {@code false}.
	 * @throws RuntimeException if an error occurs while processing the action, causing a rollback.
	 */
	@Transactional(rollbackOn = RuntimeException.class)
	public boolean recordingActionOnProposal(QuoteProposalActionReq req) {
		try {		
			
			if(ACTION_ASSIGNED.equals(req.getActionTaken())) {
				 // Create a new entry in the workflow for the assigned proposal action
		        WorkflowTracking workflowTracking = workflowService.createNewEntryInWorkflow(req);
		        if (workflowTracking == null) {
		        	throw new RuntimeException("Failed to create assigned workflow entry");
		        }

		        // Update last action in quote proposal		        
		        QuoteProposal quoteProposal = updateLastActionOnProposal(req);
		        if(quoteProposal == null) {
		        	throw new RuntimeException("Failed to update last action in quote proposal");
		        }
		        
		        // create entries in workflow factor rate request for each action
		        List<WorkflowFactorRateRequestDetail> workflowFactorRateList = workflowFactorService.createEntryInWorkflowFactorRateForEachTaken(
		        		quoteProposal.getRequestReferenceNo(), quoteProposal.getProposalId(), workflowTracking.getWorkflowId());
		        if(workflowFactorRateList == null) {
		        	throw new RuntimeException("Failed to create entries in workflow factor rate request details");
		        }

		        // Successfully processed the assigned action
		        return true;
			}
			
			if(ACTION_REVOKED.equals(req.getActionTaken())) {
		        // Take action and create a new record in WorkflowTracking
		        WorkflowTracking workflowTracking = workflowService.createNewEntryInWorkflow(req);
		        if (workflowTracking == null) {
		        	throw new RuntimeException("Failed to create revoked workflow entry");
		        }

		        // Update last action in quote proposal		        
		        QuoteProposal quoteProposal = updateLastActionOnProposal(req);
		        if(quoteProposal == null) {
		        	throw new RuntimeException("Failed to update last action in quote proposal");
		        }
		        
		        // create entries in workflow factor rate request for each action
		        List<WorkflowFactorRateRequestDetail> workflowFactorRateList = workflowFactorService.createEntryInWorkflowFactorRateForEachTaken(
		        		quoteProposal.getRequestReferenceNo(), quoteProposal.getProposalId(), workflowTracking.getWorkflowId());
		        if(workflowFactorRateList == null) {
		        	throw new RuntimeException("Failed to create entries in workflow factor rate request details");
		        }

		        // Successfully processed the revoked action
		        return true;
			}
			
			if(ACTION_APPROVED.equals(req.getActionTaken())) {
				  // Verify if the user has authority to finalize the proposal
		        boolean authorityToFinalize = approverService.verifyAuthorityToFinalize(req.getCompanyId(), 
		                req.getProductId(), req.getLoginId());
		        
		        if (authorityToFinalize) {
		            // Set Proposal status to ACCEPTED
		            QuoteProposal quoteProposal = setStatusOfProposal(req);
		            if (quoteProposal == null) {
			        	throw new RuntimeException("Failed to set proposal status to accepted");
		            }
		            
		            // Update Final Referral Status
		            QuoteUpdateRes referralStatus = finalUpdateOfReferralStatus(req);
		            if(referralStatus == null) {
		            	throw new RuntimeException("Failed to update final approved referral status");
		            }
		        }

		        // Create a record in the WorkflowTracking
		        WorkflowTracking workflowTracking = workflowService.createNewEntryInWorkflow(req);
		        if (workflowTracking == null) {
		        	throw new RuntimeException("Failed to create approved workflow entry");
		        }

		        // Update last action in quote proposal		        
		        QuoteProposal quoteProposal = updateLastActionOnProposal(req);
		        if(quoteProposal == null) {
		        	throw new RuntimeException("Failed to update last action in quote proposal");
		        }
		        
		        // create entries in workflow factor rate request for each action
		        List<WorkflowFactorRateRequestDetail> workflowFactorRateList = workflowFactorService.createEntryInWorkflowFactorRateForEachTaken(
		        		quoteProposal.getRequestReferenceNo(), quoteProposal.getProposalId(), workflowTracking.getWorkflowId());
		        if(workflowFactorRateList == null) {
		        	throw new RuntimeException("Failed to create entries in workflow factor rate request details");
		        }

		        // Successfully processed the approved action
		        return true;
			}
			
			if(ACTION_REJECTED.equals(req.getActionTaken())) {
		        // Set Proposal Declined status
		        QuoteProposal rejectedProposal = setStatusOfProposal(req);
		        if (rejectedProposal == null) {
		        	throw new RuntimeException("Failed to set proposal status to rejected");
		        }

	            // Update Final Referral Status
	            QuoteUpdateRes referralStatus = finalUpdateOfReferralStatus(req);
	            if(referralStatus == null) {
	            	throw new RuntimeException("Failed to update final rejected referral status");
	            }

		        // Create a record in WorkflowTracking
		        WorkflowTracking workflowTracking = workflowService.createNewEntryInWorkflow(req);
		        if (workflowTracking == null) {
		        	throw new RuntimeException("Failed to create rejected workflow entry");
		        }

		        // Update last action in quote proposal		        
		        QuoteProposal quoteProposal = updateLastActionOnProposal(req);
		        if(quoteProposal == null) {
		        	throw new RuntimeException("Failed to update last action in quote proposal");
		        }
		        
		        // create entries in workflow factor rate request for each action
		        List<WorkflowFactorRateRequestDetail> workflowFactorRateList = workflowFactorService.createEntryInWorkflowFactorRateForEachTaken(
		        		quoteProposal.getRequestReferenceNo(), quoteProposal.getProposalId(), workflowTracking.getWorkflowId());
		        if(workflowFactorRateList == null) {
		        	throw new RuntimeException("Failed to create entries in workflow factor rate request details");
		        }
		        
		        // Successfully processed the rejected action
		        return true;

			}
			if(ACTION_ESCALATED.equals(req.getActionTaken())) {		        	
	            // Set Proposal status to ESCALATED
				WorkflowTracking workflowTracking = workflowService.createNewEntryInWorkflow(req);
				if(workflowTracking == null) {
		        	throw new RuntimeException("Failed to create escalated workflow entry");
				}	

				// Update last action in quote proposal		        
		        QuoteProposal quoteProposal = updateLastActionOnProposal(req);
		        if(quoteProposal == null) {
		        	throw new RuntimeException("Failed to update last action in quote proposal");
		        }
		        
		        // create entries in workflow factor rate request for each action
		        List<WorkflowFactorRateRequestDetail> workflowFactorRateList = workflowFactorService.createEntryInWorkflowFactorRateForEachTaken(
		        		quoteProposal.getRequestReferenceNo(), quoteProposal.getProposalId(), workflowTracking.getWorkflowId());
		        if(workflowFactorRateList == null) {
		        	throw new RuntimeException("Failed to create entries in workflow factor rate request details");
		        }

				// Successfully processed the escalated action
				return true;
			}
			
			
			//If Action is not mentioned above
			return false;
		} catch (Exception e) {
	        log.error("Exception occurred while processing proposal action for proposal ID: {}: {}", 
	                  req.getProposalId(), e.getMessage(), e);
	        return false;
		}		
	}
	
	
	/**
	 * Creates a new quote proposal and logs the first workflow entry for the proposal.
	 * This method performs the following steps:
	 * <ol>
	 *     <li>Creates a new proposal based on the provided {@link QuoteProposalSaveReq} object.</li>
	 *     <li>If the proposal is successfully created, logs the first entry in the workflow using the 
	 *     {@link WorkflowService#firstTimeEntryInWorkflow(QuoteProposal)} method.</li>
	 * </ol>
	 * 
	 * This method is annotated with {@link Transactional} to ensure that the database operations are 
	 * rolled back if a {@link RuntimeException} occurs during the execution.
	 *
	 * @param req the {@link QuoteProposalSaveReq} object containing the details for creating a new proposal.
	 *            The request must contain the necessary information such as company ID, product ID, 
	 *            customer reference number, request reference number, and quote number.
	 * @throws RuntimeException if an unexpected error occurs during the process, which triggers a rollback.
	 * 
	 * @see WorkflowService#firstTimeEntryInWorkflow(QuoteProposal)
	 */
	@Transactional(rollbackOn = RuntimeException.class)
	public void createQuoteProposalAndFirstWorkflowEntry(QuoteProposalSaveReq req) {
		try {
			//Create new quote proposal
			QuoteProposal savedProposal = createNewProposal(req);
			 if(savedProposal == null) {
		        	throw new RuntimeException("Failed to create New Quote Proposal");
		        }
			 
	        // Create a record in WorkflowTracking
			WorkflowTracking workflowTracking = workflowService.firstTimeEntryInWorkflow(savedProposal);
			if(workflowTracking == null) {
	        	throw new RuntimeException("Failed to create NewQuote workflow entry");
			}
			
	        // create entries in workflow factor rate request for each action
	        List<WorkflowFactorRateRequestDetail> workflowFactorRateList = workflowFactorService.createEntryInWorkflowFactorRateForEachTaken(
	        		savedProposal.getRequestReferenceNo(), savedProposal.getProposalId(), workflowTracking.getWorkflowId());
	        if(workflowFactorRateList == null) {
	        	throw new RuntimeException("Failed to create entries in workflow factor rate request details");
	        }

		} catch (Exception e) {
			log.error("Exception : {}",e.getMessage(), e);
		}		
	}
	
	
	/**
	 * Creates a new proposal based on the provided {@link QuoteProposalSaveReq} object. 
	 * If a proposal with the same company ID, product ID, customer reference number, 
	 * request reference number, and quote number does not already exist, the method will 
	 * create a new proposal with a generated proposal ID, set its status to "PROCESSING", 
	 * and store the creation timestamp.
	 *
	 * @param req a {@link QuoteProposalSaveReq} object containing the details required to create a new proposal.
	 *            The object must include company ID, product ID, customer reference number, request reference 
	 *            number, and quote number to ensure the proposal is created correctly.
	 * @return a {@link QuoteProposal} object representing the newly created proposal if successful, 
	 *         or {@code null} if a proposal with the same identifiers already exists.
	 * @throws IllegalArgumentException if the provided {@link QuoteProposalSaveReq} is null or contains invalid data.
	 * @throws RuntimeException if an error occurs while saving the proposal to the repository.
	 */
	public QuoteProposal createNewProposal(QuoteProposalSaveReq req) {
		try {
			Optional<QuoteProposal> optProposal = proposalRepo
					.findByCompanyIdAndProductIdAndCustomerReferenceNoAndRequestReferenceNoAndQuoteNo(
					req.getCompanyId(), req.getProductId(), req.getCustomerReferenceNo(),
					req.getRequestReferenceNo(), req.getQuoteNo());
			
			if(optProposal.isEmpty()) {				
				QuoteProposal newProposal = mapper.map(req, QuoteProposal.class);
				newProposal.setProposalId(proposalIdGenerator(req.getCompanyId(), req.getProductId()));
				newProposal.setProposalStatus(STATUS_PROCESSING);
				newProposal.setCreatedOn(LocalDateTime.now());
				
				return proposalRepo.saveAndFlush(newProposal);
			}
			return null;
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
			return null;
		}
		
	}
	

	/**
	 * Generates a new proposal ID based on the maximum proposal ID found for the specified company and product.
	 * 
	 * <p>
	 * This method queries the database to find the proposal with the highest ID for the given company and product,
	 * and increments the ID by 1 to generate a new proposal ID. If no proposal is found (i.e., no existing proposal 
	 * for the given company and product), the method returns 1 as the new proposal ID.
	 * </p>
	 *
	 * @param companyId the ID of the company for which the proposal ID is being generated.
	 * @param productId the ID of the product for which the proposal ID is being generated.
	 * @return the generated proposal ID, which is one greater than the highest existing proposal ID, or 1 if no 
	 *         proposals exist for the specified company and product.
	 * 
	 * @throws Exception if an error occurs during the process of generating the proposal ID (though not explicitly 
	 *                   thrown in this implementation).
	 * 
	 * <p>
	 * The following actions are performed by this method:
	 * <ul>
	 *   <li>Fetches the highest proposal ID for the specified company and product from the database.</li>
	 *   <li>If a proposal ID exists, it increments the value by 1 to generate a new proposal ID.</li>
	 *   <li>If no proposal ID exists, it returns 1 as the starting proposal ID.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see QuoteProposal
	 */
	private Long proposalIdGenerator(Integer companyId, Integer productId) {
		QuoteProposal maxIdProposal = proposalRepo.findTopByCompanyIdAndProductIdOrderByProposalIdDesc(
				companyId, productId);
		
		if(maxIdProposal != null) {
			long newId = maxIdProposal.getProposalId()+1L; 
			return newId;
		}
		else {return 1L;}
	}
	
			
	/**
	 * Updates the status of a proposal based on the provided action and remarks.
	 * If the action is "APPROVED", the proposal status is set to "ACCEPTED". 
	 * If the action is "REJECTED", the status is set to "DECLINED". 
	 * The proposal is finalized with the current timestamp and remarks.
	 *
	 * @param req a {@link QuoteProposalActionReq} object containing the details needed to update 
	 *            the proposal status, including company ID, product ID, proposal ID, action taken, 
	 *            and action remarks.
	 * @return a {@link QuoteProposal} object reflecting the updated status if the proposal exists 
	 *         and the update is successful, or {@code null} if the proposal is not found or an exception occurs.
	 * @throws IllegalArgumentException if the {@link QuoteProposalActionReq} object is null or invalid.
	 * @throws RuntimeException if an exception occurs during the proposal status update process.
	 */
	public QuoteProposal setStatusOfProposal(QuoteProposalActionReq req) {
		try {
			Optional<QuoteProposal> optProposal = proposalRepo.findByCompanyIdAndProductIdAndProposalId(
					req.getCompanyId(), req.getProductId(), req.getProposalId());
			
			if(optProposal.isPresent()){
				QuoteProposal quoteProposal = optProposal.get();
				if(ACTION_APPROVED.equals(req.getActionTaken())) {
					quoteProposal.setProposalStatus(STATUS_ACCEPTED);
				}
				if(ACTION_REJECTED.equals(req.getActionTaken())) {
					quoteProposal.setProposalStatus(STATUS_DECLINED);
				}
				quoteProposal.setFinalizedOn(LocalDateTime.now());
				quoteProposal.setRemarks(req.getActionRemarks());
				
				return proposalRepo.saveAndFlush(quoteProposal);		
			}
			else {return null;}
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage());
			return null;
		}						
	}
	
	/**
	 * Performs the final update of the referral status for a given proposal based on the provided 
	 * {@link QuoteProposalActionReq}. If the proposal exists, this method prepares the referral 
	 * status request and invokes the referral status update service.
	 *
	 * @param req a {@link QuoteProposalActionReq} object containing the details needed to update the 
	 *            referral status, including company ID, product ID, proposal ID, login ID, 
	 *            action remarks, action taken, and commission details.
	 * @return a {@link QuoteUpdateRes} object containing the result of the referral status update if 
	 *         the proposal exists and the update is successful, or {@code null} otherwise.
	 * @throws IllegalArgumentException if the {@link QuoteProposalActionReq} object is null or invalid.
	 * @throws RuntimeException if an exception occurs during the referral status update process.
	 */
	public QuoteUpdateRes finalUpdateOfReferralStatus(QuoteProposalActionReq req) {
		try {
			Optional<QuoteProposal> optProposal = proposalRepo.findByCompanyIdAndProductIdAndProposalId(
					req.getCompanyId(), req.getProductId(), req.getProposalId());
			
			if(optProposal.isPresent()) {
		        AdminReferalStatusReq referral = new AdminReferalStatusReq();
		        
		        referral.setAdminLoginId(req.getLoginId());
		        referral.setAdminRemarks(req.getActionRemarks());
		        referral.setCommissionModifyYn(req.getCommissionModifyYn());
		        referral.setCommissionPercent(String.valueOf(req.getCommissionPercent()));
		        referral.setCompanyId(String.valueOf(req.getCompanyId()));
		        referral.setProductId(String.valueOf(req.getProductId()));
		        
		        if(ACTION_APPROVED.equals(req.getActionTaken())) {
		        	referral.setStatus(REFERRAL_APPROVED);
				}
				if(ACTION_REJECTED.equals(req.getActionTaken())) {
					referral.setStatus(REFERRAL_REJECTED);
					referral.setRejectReason(req.getActionRemarks());
				}    
		        
		        referral.setRequestReferenceNo(optProposal.get().getRequestReferenceNo());
		        
		        return quoteService.updateReferralStatus(referral);
			}
			else {return null;}			
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage());
			return null;
		}
		
  
	}
	
	
	/**
	 * Updates the "last action" details on a {@link QuoteProposal} entity based on the provided 
	 * {@link QuoteProposalActionReq} request. If the proposal exists, this method sets the 
	 * last action performed by the user and the timestamp of the action, then saves and flushes 
	 * the updated entity to the repository.
	 *
	 * @param req a {@link QuoteProposalActionReq} object containing the company ID, product ID, 
	 *            proposal ID, and the login ID of the user performing the action.
	 * @return the updated {@link QuoteProposal} object if the proposal exists, or {@code null} 
	 *         if no proposal matches the specified criteria.
	 * @throws IllegalArgumentException if the {@link QuoteProposalActionReq} object is null or invalid.
	 *
	 */
	public QuoteProposal updateLastActionOnProposal(QuoteProposalActionReq req) {
		Optional<QuoteProposal> optProposal = proposalRepo.findById(
				new QuoteProposalPK(req.getCompanyId(), req.getProductId(), req.getProposalId())
				);
		if(optProposal.isPresent()) {
			QuoteProposal quoteProposal = optProposal.get();
			quoteProposal.setLastActionBy(req.getLoginId());
			quoteProposal.setLastActionOn(LocalDateTime.now());
			
			return proposalRepo.saveAndFlush(quoteProposal);
		}		
		return null;		
	}
	
	
	/**
	 * Retrieves a specific {@link QuoteProposalRes} object by its company ID, product ID, 
	 * and proposal ID. The method fetches the proposal using the repository and maps it 
	 * to a response object if found.
	 *
	 * @param companyId the ID of the company to which the proposal belongs.
	 * @param productId the ID of the product to which the proposal belongs.
	 * @param proposalId the unique ID of the proposal to be retrieved.
	 * @return an {@link Optional} containing the {@link QuoteProposalRes} object if found, 
	 *         or an empty {@link Optional} if no proposal matches the specified criteria.
	 * @throws IllegalArgumentException if any of the parameters are null or invalid.
	 *
	 * Example Usage:
	 * <pre>
	 * {@code
	 * Optional<QuoteProposalRes> proposalRes = getProposal(1, 101, 1001L);
	 * }
	 * </pre>
	 */
	public Optional<QuoteProposalRes> getProposal(Integer companyId, Integer productId, Long proposalId) {
		Optional<QuoteProposal> optProposal = proposalRepo.findByCompanyIdAndProductIdAndProposalId(
				companyId, productId, proposalId);
		
		return optProposal.map(pro -> mapper.map(pro, QuoteProposalRes.class));
	}
	
			
	/**
	 * Retrieves a list of {@link QuoteProposal} objects at the entry level that meet the specified 
	 * criteria. The method filters proposals based on company ID, product ID, processing status, 
	 * sum insured range, and the "NEWQUOTE" action in the workflow tracking system. It ensures 
	 * the proposals match the latest action date recorded in the workflow tracking system.
	 *
	 * @param companyId the ID of the company to which the proposals belong.
	 * @param productId the ID of the product to which the proposals belong.
	 * @param sumInsuredStart the minimum value of the sum insured range.
	 * @param sumInsuredEnd the maximum value of the sum insured range.
	 * @return a list of {@link QuoteProposal} objects that match the specified criteria.
	 * @throws Exception if any errors occur during query execution.
	 * @throws IllegalArgumentException if any of the parameters are null or invalid.
	 *
	 * Example Usage:
	 * <pre>
	 * {@code
	 * List<QuoteProposal> proposals = findIncomingProposalsForEntryLevel(1, 101, BigDecimal.valueOf(50000), BigDecimal.valueOf(100000));
	 * }
	 * </pre>
	 */
	private List<QuoteProposal> findIncomingProposalsForEntryLevel(Integer companyId, Integer productId, 
	        BigDecimal sumInsuredStart, BigDecimal sumInsuredEnd) throws Exception{

	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<QuoteProposal> query = cb.createQuery(QuoteProposal.class);
	    Root<QuoteProposal> proposalRoot = query.from(QuoteProposal.class);

	    // Perform a LEFT JOIN between Proposal and WorkflowTracking
	    Join<QuoteProposal, WorkflowTracking> workflowJoin = proposalRoot.join("workflowRef", JoinType.LEFT);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(proposalRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(proposalRoot.get("productId"), productId);
	    Predicate statusFilter = cb.equal(proposalRoot.get("proposalStatus"), STATUS_PROCESSING);
	    
	    // Sum Insured Within Specified Range
	    Predicate sumInsuredStartFilter = cb.greaterThanOrEqualTo(proposalRoot.get("sumInsured"), sumInsuredStart);
	    Predicate sumInsuredEndFilter = cb.lessThanOrEqualTo(proposalRoot.get("sumInsured"), sumInsuredEnd);
	    
	    // Filter NEWQUOTE in WorkflowTracking
	    Predicate newQuoteFilter = cb.equal(workflowJoin.get("actionTaken"), NEW_QUOTE);
       
	    // Subquery to get the latest actionTakenOn for the Proposal
	    Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
	    Root<WorkflowTracking> subRoot = subquery.from(WorkflowTracking.class);
	    subquery.select(cb.greatest(subRoot.get("actionTakenOn")));
	    subquery.where(
	            cb.equal(subRoot.get("companyId"), proposalRoot.get("companyId")),
	            cb.equal(subRoot.get("productId"), proposalRoot.get("productId")),
	            cb.equal(subRoot.get("proposalId"), proposalRoot.get("proposalId"))
	    );

	    // WorkflowTracking Action Taken On matches the subquery result
	    Predicate actionTakenOnFilter = cb.equal(workflowJoin.get("actionTakenOn"), subquery);
	    
	    // Combine all filters
	    query.where(cb.and( companyFilter, productFilter, statusFilter,	newQuoteFilter, 
	    		sumInsuredStartFilter, sumInsuredEndFilter, actionTakenOnFilter ));

	    // Execute query and return the result list
	    return entityManager.createQuery(query).getResultList();
	}
	
	
	/**
	 * Retrieves a list of {@link QuoteProposal} objects that have been escalated to the top-level 
	 * for further processing. The method filters proposals based on the company ID, product ID, 
	 * and the "processing" status. It ensures the proposals match the latest escalation action 
	 * recorded in the workflow tracking system.
	 *
	 * @param companyId the ID of the company to which the proposals belong.
	 * @param productId the ID of the product to which the proposals belong.
	 * @return a list of {@link QuoteProposal} objects that meet the specified criteria.
	 * @throws Exception if any errors occur during query execution.
	 * @throws IllegalArgumentException if the provided companyId or productId is null or invalid.
	 *
	 * Example Usage:
	 * <pre>
	 * {@code
	 * List<QuoteProposal> proposals = findingIncomingProposalsForTopLevel(1, 101);
	 * }
	 * </pre>
	 */	
	private List<QuoteProposal> findingIncomingProposalsForTopLevel(Integer companyId, Integer productId) throws Exception{

	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<QuoteProposal> query = cb.createQuery(QuoteProposal.class);
	    Root<QuoteProposal> proposalRoot = query.from(QuoteProposal.class);

	    // Perform a INNER JOIN between Proposal and WorkflowTracking
	    Join<QuoteProposal, WorkflowTracking> workflowJoin = proposalRoot.join("workflowRef", JoinType.INNER);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(proposalRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(proposalRoot.get("productId"), productId);
	    Predicate statusFilter = cb.equal(proposalRoot.get("proposalStatus"), STATUS_PROCESSING);
	    
	    // Filter for the "ESCALATED" action in WorkflowTracking
	    Predicate escalationActionFilter = cb.equal(workflowJoin.get("actionTaken"), ACTION_ESCALATED);

	    // Subquery to get the latest actionTakenOn for the Proposal
	    Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
	    Root<WorkflowTracking> subRoot = subquery.from(WorkflowTracking.class);
	    subquery.select(cb.greatest(subRoot.get("actionTakenOn")));
	    subquery.where(
	            cb.equal(subRoot.get("companyId"), proposalRoot.get("companyId")),
	            cb.equal(subRoot.get("productId"), proposalRoot.get("productId")),
	            cb.equal(subRoot.get("proposalId"), proposalRoot.get("proposalId"))
	    );

	    // WorkflowTracking actionTakenOn matches the subquery result
	    Predicate actionTakenOnFilter = cb.equal(workflowJoin.get("actionTakenOn"), subquery);

	    // Combine all filters (predicates)
	    query.where(cb.and( companyFilter, productFilter, statusFilter,
	            escalationActionFilter, actionTakenOnFilter ));

	    // Execute query and return the result list
	    return entityManager.createQuery(query).getResultList();
	}

	
	/**
	 * Retrieves a list of {@link QuoteProposal} objects that are in a "processing" status and 
	 * are transitioning to the next level of approval. The method filters proposals based on 
	 * company ID, product ID, sum insured range, previous approver level, and workflow actions.
	 * 
	 * The method ensures that the proposals match the latest action taken date from the workflow 
	 * tracking system and that the previous level approver has approved the proposals.
	 *
	 * @param companyId the ID of the company to which the proposals belong.
	 * @param productId the ID of the product to which the proposals belong.
	 * @param prevLevel the hierarchy level of the previous approver for filtering proposals.
	 * @param sumInsuredStart the minimum value of the sum insured range.
	 * @param sumInsuredEnd the maximum value of the sum insured range.
	 * @return a list of {@link QuoteProposal} objects that meet the specified criteria.
	 * @throws Exception if any errors occur during query execution.
	 * @throws IllegalArgumentException if any parameters are null or invalid.
	 *
	 * Example Usage:
	 * <pre>
	 * {@code
	 * List<QuoteProposal> proposals = findIncomingProposalsForRemainingLevels(1, 101, 2, new BigDecimal("100000"), new BigDecimal("500000"));
	 * }
	 * </pre>
	 */
	private List<QuoteProposal> findIncomingProposalsForRemainingLevels(Integer companyId, 
	        Integer productId, Integer prevLevel, BigDecimal sumInsuredStart, BigDecimal sumInsuredEnd) throws Exception {

	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<QuoteProposal> query = cb.createQuery(QuoteProposal.class);
	    Root<QuoteProposal> proposalRoot = query.from(QuoteProposal.class);

	    // Perform a INNER JOIN between Proposal and WorkflowTracking
	    Join<QuoteProposal, WorkflowTracking> workflowJoin = proposalRoot.join("workflowRef", JoinType.INNER);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(proposalRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(proposalRoot.get("productId"), productId);
	    Predicate statusFilter = cb.equal(proposalRoot.get("proposalStatus"), STATUS_PROCESSING);

	    // Sum Insured Within Specified Range
	    Predicate sumInsuredStartFilter = cb.greaterThanOrEqualTo(proposalRoot.get("sumInsured"), sumInsuredStart);
	    Predicate sumInsuredEndFilter = cb.lessThanOrEqualTo(proposalRoot.get("sumInsured"), sumInsuredEnd);

	    // Approver Level Filter (Hierarchy Value)
	    Predicate approverLevelFilter = cb.equal(workflowJoin.get("hierarchyValue"), prevLevel);

	    // WorkflowTracking Action "APPROVED"
	    Predicate approvedActionFilter = cb.equal(workflowJoin.get("actionTaken"), ACTION_APPROVED);

	    // Subquery to get the latest actionTakenOn for the Proposal
	    Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
	    Root<WorkflowTracking> subRoot = subquery.from(WorkflowTracking.class);
	    subquery.select(cb.greatest(subRoot.get("actionTakenOn")));
	    subquery.where(
	            cb.equal(subRoot.get("companyId"), proposalRoot.get("companyId")),
	            cb.equal(subRoot.get("productId"), proposalRoot.get("productId")),
	            cb.equal(subRoot.get("proposalId"), proposalRoot.get("proposalId"))
	    );

	    // WorkflowTracking Action Date Time matches the subquery result
	    Predicate actionTakenOnFilter = cb.equal(workflowJoin.get("actionTakenOn"), subquery);

	    // Combine all filters (predicates)
	    query.where(cb.and( companyFilter, productFilter, statusFilter,
	    		sumInsuredStartFilter, sumInsuredEndFilter, approverLevelFilter,
	            approvedActionFilter, actionTakenOnFilter ));

	    // Execute query and return the result list
	    return entityManager.createQuery(query).getResultList();
	}
	
	
	/**
	 * Retrieves a list of {@link QuoteProposal} objects that have been revoked 
	 * and are now transitioning to an incoming status. The method filters proposals 
	 * based on company ID, product ID, sum insured range, approver level, and workflow actions.
	 * 
	 * The method ensures that the proposals match the latest action taken date 
	 * from the workflow tracking system.
	 *
	 * @param companyId the ID of the company to which the proposals belong.
	 * @param productId the ID of the product to which the proposals belong.
	 * @param sameLevelValue the hierarchy level value of the approver for filtering proposals.
	 * @param sumInsuredStart the minimum value of the sum insured range.
	 * @param sumInsuredEnd the maximum value of the sum insured range.
	 * @return a list of {@link QuoteProposal} objects that meet the specified criteria.
	 * @throws Exception if any errors occur during query execution.
	 * @throws IllegalArgumentException if any parameters are null or invalid.
	 *
	 * Example Usage:
	 * <pre>
	 * {@code
	 * List<QuoteProposal> proposals = revokedProposalBecomingIncoming(1, 101, 2, new BigDecimal("100000"), new BigDecimal("500000"));
	 * }
	 * </pre>
	 */
	private List<QuoteProposal> revokedProposalBecomingIncoming(Integer companyId, Integer productId, 
	        Integer sameLevelValue, BigDecimal sumInsuredStart, BigDecimal sumInsuredEnd) throws Exception {

	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<QuoteProposal> query = cb.createQuery(QuoteProposal.class);
	    Root<QuoteProposal> proposalRoot = query.from(QuoteProposal.class);

	    // Perform an INNER JOIN between Proposal and WorkflowTracking
	    Join<QuoteProposal, WorkflowTracking> workflowJoin = proposalRoot.join("workflowRef", JoinType.INNER);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(proposalRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(proposalRoot.get("productId"), productId);
	    Predicate statusFilter = cb.equal(proposalRoot.get("proposalStatus"), STATUS_PROCESSING);

	    // Sum Insured Within Specified Range
	    Predicate sumInsuredStartFilter = cb.greaterThanOrEqualTo(proposalRoot.get("sumInsured"), sumInsuredStart);
	    Predicate sumInsuredEndFilter = cb.lessThanOrEqualTo(proposalRoot.get("sumInsured"), sumInsuredEnd);

	    // Approver Level Filter (Hierarchy Value)
	    Predicate approverLevelFilter = cb.equal(workflowJoin.get("hierarchyValue"), sameLevelValue);

	    // WorkflowTracking Action "REVOKED"
	    Predicate revokedActionFilter = cb.equal(workflowJoin.get("actionTaken"), ACTION_REVOKED);

	    // Subquery to get the latest actionTakenOn for the Proposal
	    Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
	    Root<WorkflowTracking> subRoot = subquery.from(WorkflowTracking.class);
	    subquery.select(cb.greatest(subRoot.get("actionTakenOn")));
	    subquery.where(
	            cb.equal(subRoot.get("companyId"), proposalRoot.get("companyId")),
	            cb.equal(subRoot.get("productId"), proposalRoot.get("productId")),
	            cb.equal(subRoot.get("proposalId"), proposalRoot.get("proposalId"))
	    );

	    // WorkflowTracking actionTakenOn matches the subquery result
	    Predicate actionTakenOnFilter = cb.equal(workflowJoin.get("actionTakenOn"), subquery);

	    // Combine all filters (predicates)
	    query.where(cb.and( companyFilter, productFilter, statusFilter,
	            sumInsuredStartFilter, sumInsuredEndFilter, approverLevelFilter,
	            revokedActionFilter, actionTakenOnFilter ));

	    // Execute query and return the result list
	    return entityManager.createQuery(query).getResultList();
	}
	
	
	/**
	 * Retrieves a list of {@link QuoteProposal} objects assigned to a specific approver 
	 * based on the company ID, product ID, and login ID. This method performs a database 
	 * query to fetch proposals that are in a "processing" status and assigned to the approver 
	 * in the workflow tracking system.
	 *
	 * The method also ensures that the proposals are filtered based on the latest action 
	 * taken date for the workflow.
	 *
	 * @param companyId the ID of the company for which proposals are being fetched.
	 * @param productId the ID of the product for which proposals are being fetched.
	 * @param loginId the login ID of the user (approver) to whom the proposals are assigned.
	 * @return a list of {@link QuoteProposal} objects that match the specified criteria.
	 * @throws Exception if any errors occur during query execution.
	 *
	 * Example Usage:
	 * <pre>
	 * {@code
	 * List<QuoteProposal> proposals = findingAssignedProposalsForEachApprover(1, 101, "user123");
	 * }
	 * </pre>
	 */
	private List<QuoteProposal> findingAssignedProposalsForEachApprover(
			Integer companyId, Integer productId, String loginId) throws Exception {

	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<QuoteProposal> query = cb.createQuery(QuoteProposal.class);
	    Root<QuoteProposal> proposalRoot = query.from(QuoteProposal.class);

	    // Perform a INNER JOIN between Proposal and WorkflowTracking
	    Join<QuoteProposal, WorkflowTracking> workflowJoin = proposalRoot.join("workflowRef", JoinType.INNER);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(proposalRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(proposalRoot.get("productId"), productId);
	    Predicate statusFilter = cb.equal(proposalRoot.get("proposalStatus"), STATUS_PROCESSING);
	    
	    // Filter for the specific loginId and assigned action in WorkflowTracking
	    Predicate loginIdFilter = cb.equal(workflowJoin.get("loginId"), loginId);
	    Predicate assignedActionFilter = cb.equal(workflowJoin.get("actionTaken"), ACTION_ASSIGNED);

	    // Subquery to get the latest actionTakenOn for the Proposal
	    Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
	    Root<WorkflowTracking> subRoot = subquery.from(WorkflowTracking.class);
	    subquery.select(cb.greatest(subRoot.get("actionTakenOn")));
	    subquery.where(
	            cb.equal(subRoot.get("companyId"), proposalRoot.get("companyId")),
	            cb.equal(subRoot.get("productId"), proposalRoot.get("productId")),
	            cb.equal(subRoot.get("proposalId"), proposalRoot.get("proposalId"))
	    );

	    // WorkflowTracking actionTakenOn matches the subquery result
	    Predicate actionTakenOnFilter = cb.equal(workflowJoin.get("actionTakenOn"), subquery);

	    // Combine all filters (predicates)
	    query.where(cb.and( companyFilter, productFilter, statusFilter,
	            loginIdFilter, assignedActionFilter, actionTakenOnFilter ));

	    // Execute query and return the result list
	    return entityManager.createQuery(query).getResultList();
	}

	
	/**
	 * Retrieves a list of {@link QuoteProposal} objects for a specific company, product, 
	 * login ID, and action taken. This method performs a database query to fetch proposals 
	 * that match the provided criteria, including filters for company ID, product ID, login ID, 
	 * and action taken within the associated workflow.
	 *
	 * @param companyId the ID of the company for which proposals are being fetched.
	 * @param productId the ID of the product for which proposals are being fetched.
	 * @param loginId the login ID of the user associated with the workflow.
	 * @param action the action taken in the workflow for which proposals are being filtered.
	 * @return a list of {@link QuoteProposal} objects that match the specified criteria.
	 * @throws Exception if any errors occur during query execution.
	 * @throws IllegalArgumentException if any of the parameters are null or invalid.
	 * 
	 * Example Usage:
	 * <pre>
	 * {@code
	 * List<QuoteProposal> proposals = findingProposalsForEachApproverByActionTaken(1, 101, "user123", "APPROVED");
	 * }
	 * </pre>
	 */
	private List<QuoteProposal> findingProposalsForEachApproverByActionTaken(
			Integer companyId, Integer productId, String loginId, String action) throws Exception {

	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<QuoteProposal> query = cb.createQuery(QuoteProposal.class);
	    Root<QuoteProposal> proposalRoot = query.from(QuoteProposal.class);

	    // Perform a INNER JOIN between Proposal and WorkflowTracking
	    Join<QuoteProposal, WorkflowTracking> workflowJoin = proposalRoot.join("workflowRef", JoinType.INNER);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(proposalRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(proposalRoot.get("productId"), productId);
	    Predicate proposalFilter = cb.equal(proposalRoot.get("proposalId"), workflowJoin.get("proposalId"));
	    
	    
	    // Filter for the specific loginId and action in WorkflowTracking
	    Predicate loginIdFilter = cb.equal(workflowJoin.get("loginId"), loginId);
	    Predicate actionFilter = cb.equal(workflowJoin.get("actionTaken"), action);


	    // Combine all filters (predicates)
	    query.where(cb.and( companyFilter, productFilter, proposalFilter,
	            loginIdFilter, actionFilter ));

	    // Execute query and return the result list
	    return entityManager.createQuery(query).getResultList();
	}
	


}
