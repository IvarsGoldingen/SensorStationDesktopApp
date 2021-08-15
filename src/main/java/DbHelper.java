import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.spi.TransactionStatus;

//DataBase Helper
public class DbHelper {
	Session session;
	Transaction transaction;
	UiLogCallback logCB;
	
	public DbHelper(UiLogCallback logCB) {
		init();
		this.logCB = logCB;
	}
	
	private void commitDb() {
		if (transaction.getStatus().equals(TransactionStatus.ACTIVE)) {
			transaction.commit();
		}
	}
	
	private boolean isItemDublicate(int itemID) {
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
		if (!isItemDublicate(item.getDaily_item_id())) {
			session.save(item);
			commitDb();
		} else {
			System.out.println("Dublicate item not addig to DB");
		}
	}
	
	//Save multiple items
	public void saveItems(List <DailyAveragesItem> items) {
		logCB.log("Attemting to save items to DB");
		int itemCntr = 1;
		for (DailyAveragesItem item: items) {
			if (!isItemDublicate(item.getDaily_item_id())) {
				session.save(item);
				logCB.log("Saving item " + itemCntr);
			} else {
				logCB.log("Dublicate not saving " + itemCntr);
			}
			itemCntr++;
		}
		commitDb();
	}
	
	public ArrayList <DailyAveragesItem> getAllItems(){
		logCB.log("Getting items from DB");
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<DailyAveragesItem> criteria = builder.createQuery(DailyAveragesItem.class);
		criteria.from(DailyAveragesItem.class);
		ArrayList <DailyAveragesItem> items = (ArrayList<DailyAveragesItem>) session.createQuery(criteria).getResultList();
		logCB.log("Got " + items.size() + " items from DB");
		commitDb();
		return items;
	}
	
	private void init() {
		Configuration hibernateConf = new Configuration().configure().addAnnotatedClass(DailyAveragesItem.class);
		try {
			SessionFactory sFactory = hibernateConf.buildSessionFactory();
			session = sFactory.openSession();
			transaction = session.beginTransaction();
		} catch (Exception e) {
			System.out.println(e);
			logCB.log("Error initializing DB");
		}
	}
	
}
