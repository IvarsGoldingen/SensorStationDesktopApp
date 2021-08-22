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
public class DbHelper implements Runnable {
	private Session session;
	private Transaction transaction;
	private UiLogCallback logCB;
	private DBCallbacks dbCb;
	
	private boolean initialised = false;
	
	@Override
	
	public void run() {
		// TODO Auto-generated method stub
		init();
	}
	
	
	public DbHelper(UiLogCallback logCB, DBCallbacks dbCb) {
		this.logCB = logCB;
		this.dbCb = dbCb;
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
		int dublicateCntr = 0;
		int savedCntr = 0;
		for (DailyAveragesItem item: items) {
			if (!isItemDublicate(item.getDaily_item_id())) {
				session.save(item);
				savedCntr++;
			} else {
				dublicateCntr++;
			}
			
		}
		logCB.log("Saved " + savedCntr + " items. " +  dublicateCntr + " dublicates.");
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
	
	public void returnAllItems() {
		ArrayList <DailyAveragesItem> items = getAllItems(DailyAveragesItem.class);
		dbCb.returnDailyAvaragesList(items);
	}
	
	public <T> ArrayList<T> getAllItems(Class<T> clazz) {
		logCB.log("Getting items from DB");
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(clazz);
		criteria.from(clazz);
		ArrayList <T> items = (ArrayList<T>) session.createQuery(criteria).getResultList();
		logCB.log("Got " + items.size() + " items from DB");
		commitDb();
		return items;
	}
	
	private void init() {
		logCB.log("Initializing database");
		Configuration hibernateConf = new Configuration().configure()
				.addAnnotatedClass(DailyAveragesItem.class)
				.addAnnotatedClass(NightAveragesItem.class);
		try {
			SessionFactory sFactory = hibernateConf.buildSessionFactory();
			session = sFactory.openSession();
			transaction = session.beginTransaction();
			initialised = true;
		} catch (Exception e) {
			System.out.println(e);
			logCB.log("Error initializing DB");
			initialised = false;
		}
		logCB.log("Database initialization ended");
		returnAllItems();
	}
	
	public boolean isInitialized() {
		return initialised;
	}

	
	
}
