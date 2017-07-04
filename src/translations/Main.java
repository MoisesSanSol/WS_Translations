package translations;

public class Main {

	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		FileUpdater.updateHotcCleanFiles_StringBasedReplacement();
		
		//Dispatcher.rawAbilityMasterList();
		//Dispatcher.rawRemainingAbilityMasterList();
		//Dispatcher.getTranlationProgress("is_the_order_a_rabbit_trial_deck");
		
		System.out.println("*** Finished ***");
	}
}
