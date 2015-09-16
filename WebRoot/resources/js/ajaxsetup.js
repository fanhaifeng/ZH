$(document).ready(function(){
          $.ajaxSetup({   
                contentType:"application/x-www-form-urlencoded;charset=utf-8",   
                cache:false,   
                complete:function(XMLHttpRequest,textStatus){   
        	  		if (typeof XMLHttpRequest.responseXML == "undefined")
        	  		{
        	  			var sessionstatus=XMLHttpRequest.getResponseHeader("sessionstatus"); 
                   		if(sessionstatus=="timeout"){   
                          window.location.href="/store_b2b/login.html";
                        }  
        	  		}
                }   
              });  
});