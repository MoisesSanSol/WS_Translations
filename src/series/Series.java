package series;

public class Series {

	String setFileName;
	
	String setName;
	String setHotcName;
	String setJpName;
	String setId;
	String setFileId;
	String setType;
	String setJpType;
	String setImage;
	String setNeoStandardHotcGroup;
	String setNeoStandardJpGroup;

	String setImageUrl;
	String relevant;
	
	String setPromoFile;
	String setReleaseDate;
	
	String setLaPageId;
	String setYytPageId;
	
	String isLegacyEb;
	String isLegacySp;
	String isLegacyTd;
	
	String status;
	
	int cardCount;
	
	public void splitJpSetInfo(String jpSetInfo){
		
		String[] split = jpSetInfo.split("　");
		if(split.length == 2){
			this.setType = split[0];
			this.setJpName = split[1];
		}
		else{
			this.setType = "Error_Type";
			this.setJpName = jpSetInfo;
		}
	}
}
