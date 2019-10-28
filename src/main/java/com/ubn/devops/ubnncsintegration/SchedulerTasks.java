package com.ubn.devops.ubnncsintegration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;
import com.ubn.devops.ubnncsintegration.utility.FolderWatch;


@EnableScheduling
@Component
public class SchedulerTasks {

	@Autowired
	private FolderWatch folderWatch;

	@Autowired
	private PaymentDetailsService paymentDetailsService;
	
	public Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Scheduled(fixedDelay = 1500)
	public void watchFolder() {
		folderWatch.watchFolder();
	}

	//@Scheduled(fixedDelay = 1000)
	public void fetchAndUpdateRecords() {
		log.info("Trying to fetch payment details that have been paid and confirmed by NCS");
		List<PaymentDetails> details = paymentDetailsService.findAllPaidAndNcsConfirmed();
		log.info("Pad and confirmed detaisl fetched: "+details.size()+" records");		
		for(PaymentDetails detail: details){
			paymentDetailsService.performSweeporRetract(detail.getFcubsPostingRef(),detail.getTransactionstatus());
		}
	}

}
