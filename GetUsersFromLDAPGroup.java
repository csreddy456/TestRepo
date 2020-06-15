
//Java code to get all users in LDAP Group

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class GetUsersFromLDAPGroup {
 static String ldapSearchBase = "DC=uchad,DC=uchospitals,DC=edu";
 //static String ldapSearchBase = "CN=Benjamin Torres,OU=UCH Accounts,OU=People,OU=UCH,DC=UCHAD,DC=uchospitals,DC=edu";
 //static String ldapSearchBase = "OU=UCH Accounts,OU=People,OU=UCH,DC=UCHAD,DC=uchospitals,DC=edu";
 private static DirContext ctx = null;
 private static DirContext getActiveDirectoryContext() throws Exception {
  System.out.println("Start of GetActiveDirectoryContext"); 
  final Properties properties = new Properties();
  properties.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
  properties.put(Context.PROVIDER_URL,"ldap://ucmcdmc08dar.uchad.uchospitals.edu.:389");
 // properties.put(Context.SECURITY_AUTHENTICATION,"simple");
  properties.put(Context.SECURITY_PRINCIPAL, "cbollampally@UCHAD.UCHOSPITALS.EDU");
  properties.put(Context.SECURITY_CREDENTIALS,"UcmE@2011");
  return new InitialDirContext(properties);

 }
 public void getGroupUsers(String searchBase, String searchFilter, String returnedAttrs[], int maxResults)
 {
  Hashtable userEntries = null;
  String member="";
  try{
   System.out.println("Start of GetGroupUsers");   
   SearchControls searchCtls = new SearchControls();
   searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);       
   searchCtls.setReturningAttributes(returnedAttrs);
   ctx=getActiveDirectoryContext();
   System.out.println("LDAP Connection: COMPLETE");
   try{
    System.out.println("Search Base: "+searchBase);
    System.out.println("Search Filter: "+searchFilter);
    NamingEnumeration users = ctx.search(searchBase, searchFilter, searchCtls);

    if(users.hasMoreElements() == false){
    System.out.println("Not find any object with this filter " + searchFilter + " and searchBase " + searchBase);
    }
    
    int k = 0;
    String attValue = "";
    userEntries = new Hashtable();
    while (users.hasMoreElements()){
     if(k >= maxResults)
      break;           
     SearchResult sr = (SearchResult)users.next();
     Attributes attrs = sr.getAttributes();
	   System.out.println("attrs :"+attrs);
	 if (attrs.size() == 0){
      System.out.println("Could not find attribute " + returnedAttrs[0] + " for this object.");
     }else{
     
      try{              
       for (NamingEnumeration ae = attrs.getAll();ae.hasMore();){ 
        Attribute attr = (Attribute)ae.next(); 
        System.out.println("attr :"+attr);		
        String id = attr.getID();
		System.out.println("Id :"+id);
        for (NamingEnumeration e = attr.getAll();e.hasMore();){                      
         attValue = (String)e.next();
         if(id.equalsIgnoreCase("member")){
          member = attValue;
          System.out.println("member :"+member);
		 }
          else
          {
           System.out.println("empty");
          }
        }
       }
      }catch(NamingException e){
       System.out.println("Problem listing membership:"+e);            
      }     
     }
     k++;
    }
   }catch (NamingException e){
    System.out.println("Problem searching directory: "+e);            
   }       
   ctx.close();
   ctx=null;   
  }catch (Exception namEx){
   System.out.println("Exception while fetching the users from LDAP::"+namEx);        
  }     
  
 }
 public static void main(String args[]) throws Exception{
  System.out.println("Start of Main");  
  GetUsersFromLDAPGroup gug = new GetUsersFromLDAPGroup();
  String returnedAttrs[] = {"cn","member", "name","distinguishedName"};  
  //String returnedAttrs[] = {"member"};  
  String searchFilter="CN=OB_HR_Super_Users";
  //String searchFilter="CN=Benjamin Torres";

  gug.getGroupUsers(ldapSearchBase,searchFilter, returnedAttrs, Integer.parseInt("2000"));
 }
}