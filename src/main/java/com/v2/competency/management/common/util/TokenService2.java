package com.v2.competency.management.common.util;
import java.util.concurrent.TimeUnit;

public class TokenService2 {
	
	private static String sep = "#$#$&&";
	private static String sepPattern = "[\\#][\\$][\\#][\\$][\\&][\\&]";
	
	private static Long sessionTimeOutInDays = 1l;
	
	public static String generateToken(String role, String user) {
		
		Long time = System.currentTimeMillis();
		String input = user+sep+time+sep+role;
		String token = EncDecService.encrypt(input);
		token = role+"$$$"+token;
		return token;
	}
	
	public static String generateLongToken(String role, String user) {
			
		    Long time = System.currentTimeMillis() + (10L * 24 * 60 * 60 * 1000);
			String input = user+sep+time+sep+role;
			String token = EncDecService.encrypt(input);
			token = role+"$$$"+token;
			return token;
		}
	
	public static String validateTokenAndReturnUserInfo(String token) {
		try {
			//String patt = "[\\#\\$\\#\\$\\&\\&]";
			token = token.substring(token.indexOf("$$$")+3, token.length());
			String decrypted = EncDecService.decrypt(token);
			String dat[] = decrypted.split(sepPattern);
				if(dat.length != 3) {
					return "TOKEN_INVALID";
				}
			String user = dat[0];
			String time = dat[1];
			Long timeInL = Long.parseLong(time);
			Long now = System.currentTimeMillis();
			Long days = TimeUnit.MILLISECONDS.toDays(now - timeInL);
			if(days > sessionTimeOutInDays) {
				return "TOKEN_EXPIRED";
			}
			else{
				String uT = dat[2];
				return user+sep+uT;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("in validateTokenAndReturnUserType "+e.getMessage());
			return "TOKEN_INVALID_("+e.getMessage()+")";
		}
	}
	
	public static String validateTokenAndReturnUserInfo2(String token) {
	    try {
	        token = token.substring(token.indexOf("$$$") + 3);

	        String decrypted = EncDecService.decrypt(token);
	        String dat[] = decrypted.split(sepPattern);

	        if (dat.length != 3) {
	            return "TOKEN_INVALID";
	        }

	        String user = dat[0];
	        String time = dat[1];

	        Long timeInL = Long.parseLong(time);
	        Long now = System.currentTimeMillis();

	        Long days = TimeUnit.MILLISECONDS.toDays(now - timeInL);

	        // Calculate remaining token validity
	        Long expiryTime = timeInL +
	                TimeUnit.DAYS.toMillis(sessionTimeOutInDays + 1L);

	        Long remainingTime = expiryTime - now;

	        if (remainingTime > 0) {

	            long remainingDays = TimeUnit.MILLISECONDS.toDays(remainingTime);

	            long remainingHours = TimeUnit.MILLISECONDS.toHours(remainingTime)
	                    % 24;

	            long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime)
	                    % 60;

	            System.out.println("========== TOKEN EXPIRY INFO ==========");
	            System.out.println("User: " + user);
	            System.out.println("Remaining Days: " + remainingDays);
	            System.out.println("Remaining Hours: " + remainingHours);
	            System.out.println("Remaining Minutes: " + remainingMinutes);
	            System.out.println("=======================================");

	        } else {
	            System.out.println("TOKEN HAS EXPIRED");
	        }

	        if (days > sessionTimeOutInDays) {
	            return "TOKEN_EXPIRED";
	        } else {
	            String uT = dat[2];
	            return user + sep + uT;
	        }

	    } catch (Exception e) {
	        System.out.println("in validateTokenAndReturnUserType " + e.getMessage());
	        return "TOKEN_INVALID_(" + e.getMessage() + ")";
	    }
	}

}
