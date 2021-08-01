import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.spi.TransactionStatus;

//DataBase Helper
public class DbHelper {
	Session session;
	Transaction transaction;
	
	
	public DbHelper() {
		init();
	}
	
	private void commitDb() {
		if (transaction.getStatus().equals(TransactionStatus.ACTIVE)) {
			transaction.commit();
		}
	}
	
	private boolean isItemDubliate(int itemID) {
		DailyAveragesItem onlineDbItem = (DailyAveragesItem)session.get(DailyAveragesItem.class, itemID);
		if (onlineDbItem == null) {
			return false;
		} else {
			return true;
		}
	}
	
	//Save signle daily item
	public void saveItem(DailyAveragesItem item) {
		//check if item dublicate
		if (!isItemDubliate(item.getDaily_item_id())) {
			session.save(item);
			commitDb();
		} else {
			System.out.println("Dublicate item not addig to DB");
		}
	}
	
	//Save multiple items
	public void saveItems(List <DailyAveragesItem> items) {
		for (DailyAveragesItem item: items) {
			if (!isItemDubliate(item.getDaily_item_id())) {
				session.save(item);
			}
		}
		transaction.commit();
	}
	
	private void init() {
		Configuration hibernateConf = new Configuration().configure().addAnnotatedClass(DailyAveragesItem.class);
		SessionFactory sFactory = hibernateConf.buildSessionFactory();
		session = sFactory.openSession();
		transaction = session.beginTransaction();
	}
	
}
