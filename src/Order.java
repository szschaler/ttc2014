public  class  Order {
	
  private String Acct0 =   "26522154" ;

	
  private String ClOrdID0 =   "123456" ;

	
  private String OrdTyp0 =   "2" ;

	
  private String Px0 =   "93.25" ;

	
  private String Side0 =   "2" ;

	
  private String TransactTm0 =   "2001-09-11T09:30:47-05:00" ;

	
  private Hdr Hdr0 =   new Hdr ();

	
  private Instrmt Instrmt0 =   new Instrmt ();

	
  private OrdQty OrdQty0 =   new OrdQty ();

	

  public Order() {}

	
  
  public Order(String Acct0, String ClOrdID0, String OrdTyp0, String Px0, String Side0, String TransactTm0, Hdr Hdr0, Instrmt Instrmt0, OrdQty OrdQty0) {
   			 this.Acct0 = Acct0;
   			 this.ClOrdID0 = ClOrdID0;
   			 this.OrdTyp0 = OrdTyp0;
   			 this.Px0 = Px0;
   			 this.Side0 = Side0;
   			 this.TransactTm0 = TransactTm0;
   			 this.Hdr0 = Hdr0;
   			 this.Instrmt0 = Instrmt0;
   			 this.OrdQty0 = OrdQty0;
  }


}
