public   class  FIXML {
	
  public static void main (String[] args) {
    FIXML top = new FIXML (new Order ("26522154", "123456", "2", "93.25", "2", "2001-09-11T09:30:47-05:00", new Hdr ("N", "N", "521", "2001-09-11T09:30:47-05:00", new Sndr ("AFUNDMGR"), new Tgt ("ABROKER")), new Instrmt ("459200101", "1", "IBM"), new OrdQty ("1000")));    
  }

	
  private Order Order0 =   new Order ();

	

  public FIXML() {}

	
  
  public FIXML(Order Order0) {
   			 this.Order0 = Order0;
  }


}
