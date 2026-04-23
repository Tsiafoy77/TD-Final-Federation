package com.federation.agriculture;

import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.controller.CollectivityController;
import com.federation.agriculture.controller.MemberController;
import com.federation.agriculture.repository.*;
import com.federation.agriculture.service.CollectivityService;
import com.federation.agriculture.service.MemberService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AgricultureApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgricultureApplication.class, args);
		System.out.println("Serveur démarré sur http://localhost:8085");
	}

	@Bean
	public DatabaseConfig databaseConfig() {
		return new DatabaseConfig();
	}

	@Bean
	public MemberRepository memberRepository() {
		return new MemberRepository(databaseConfig());
	}

	@Bean
	public CollectivityRepository collectivityRepository() {
		return new CollectivityRepository(databaseConfig());
	}

	@Bean
	public MembershipFeeRepository membershipFeeRepository() {
		return new MembershipFeeRepository(databaseConfig());
	}

	@Bean
	public FinancialAccountRepository financialAccountRepository() {
		return new FinancialAccountRepository(databaseConfig());
	}

	@Bean
	public MemberPaymentRepository memberPaymentRepository() {
		return new MemberPaymentRepository(databaseConfig(), financialAccountRepository());
	}

	@Bean
	public CollectivityTransactionRepository collectivityTransactionRepository() {
		return new CollectivityTransactionRepository(databaseConfig(), memberRepository(), financialAccountRepository());
	}

	@Bean
	public MemberService memberService() {
		return new MemberService(memberRepository(), memberPaymentRepository(),
				collectivityTransactionRepository(), membershipFeeRepository());
	}

	@Bean
	public CollectivityService collectivityService() {
		return new CollectivityService(collectivityRepository(), memberRepository(), databaseConfig(),
				membershipFeeRepository(), collectivityTransactionRepository());
	}

	@Bean
	public MemberController memberController() {
		return new MemberController(memberService());
	}

	@Bean
	public CollectivityController collectivityController() {
		return new CollectivityController(collectivityService());
	}
}