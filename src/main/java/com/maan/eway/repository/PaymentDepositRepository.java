package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.PaymentDeposit;

public interface PaymentDepositRepository extends JpaRepository<PaymentDeposit, String>{

	List<PaymentDeposit> findByCbcNo(String cbcNo);

}
