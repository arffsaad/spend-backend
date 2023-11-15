package cyou.arfsd.spendbackend.Scheduled;

import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cyou.arfsd.spendbackend.Models.Instalments;
import cyou.arfsd.spendbackend.Repositories.InstalmentsRepository;

@Component
public class InstalmentScheduler {

    @Autowired
    private InstalmentsRepository instalments;

	@Scheduled(cron="0 0 0 * * *")
	public void updateInstalment() {

        Calendar dateToday = Calendar.getInstance();
        Integer day = dateToday.get(Calendar.DAY_OF_MONTH);

		// retrieve all instalments
        List<Instalments> allInstalments = instalments.findAllIncomplete();

        // for each instalment
        for (Instalments instalment : allInstalments) {
            // check if the day == due day
            if (instalment.getDueDate() == day && instalment.getMonths() > 0) {
                // calculate the monthly payment
                Integer monthlyPayment = instalment.getAmountLeft() / instalment.getMonths();

                // deduct from the total
                instalment.setAmountLeft(instalment.getAmountLeft() - monthlyPayment);

                // plus into the due
                instalment.setAmountDue(instalment.getAmountDue() + monthlyPayment);

                // minus the month
                instalment.setMonths(instalment.getMonths() - 1);

                // save
                instalments.save(instalment);
            }
        }
	}
}