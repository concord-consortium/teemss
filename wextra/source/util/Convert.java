package extra.util;



public class Convert

{

 	public static float foFloat(String s){		

		int sign = 1;

		int currIndex = 0;

		boolean endOfString = false;

        	char c = s.charAt(currIndex);

	        while( c == 32 || c == 9) {

	                currIndex++;

	                if( currIndex >= s.length() ){

	                	endOfString = true;

	                	break;

	                }

	                c = s.charAt(currIndex);

		}

		int v0 = 0;

		int vm = 0;

		int vexp = 0;

		int mainStart = 0;

		if(!endOfString){

			boolean doCorrectrion = false;

			if(c == '+'){

				doCorrectrion = true;

			}else if(c == '-'){

				sign = -1;

				doCorrectrion = true;

			}

			if(doCorrectrion){

				currIndex++;

				mainStart = currIndex;

	                	c = s.charAt(currIndex);

			}

		}

		endOfString = false;

	        while( c != '.' && c != 'e'  && c!='E') {

	                currIndex++;

	                if( currIndex >= s.length() ){

	                	endOfString = true;

	                	break;

	                }

	                c = s.charAt(currIndex);

		}

		int lengthMant = 0;

		v0 = waba.sys.Convert.toInt(s.substring(mainStart,currIndex));

		if(!endOfString){

			if(c == '.'){

				currIndex++;

				endOfString = ( currIndex >= s.length() );

				if(!endOfString){

					int startMant = currIndex;

		                	c = s.charAt(currIndex);

				        while( c != 'e'  && c!='E') {

				                currIndex++;

				                if( currIndex >= s.length() ){

				                	endOfString = true;

				                	break;

				                }

				                c = s.charAt(currIndex);

					}

					vm = waba.sys.Convert.toInt(s.substring(startMant,currIndex));

					lengthMant = currIndex - startMant;

					if(endOfString){

						lengthMant = s.length() - startMant;

					}

				}

			}

			if(!endOfString && (c == 'e'  || c=='E')){

				currIndex++;

				endOfString = ( currIndex >= s.length() );

				if(!endOfString){

					c = s.charAt(currIndex);

					if(c == '+') currIndex++;

				}

				vexp = waba.sys.Convert.toInt(s.substring(currIndex));

			}

		}

		float value = ((float)v0 + (float)vm/Maths.pow(10.0f,lengthMant))*Maths.pow(10.0f,vexp);

		if(sign < 0) value = -value;

		return value;

	}



}