package org.concord.waba.extra.util;

import waba.util.Vector;

public class CCUnit
{
	public int unitCategory  = UNIT_CAT_UNKNOWN;

	public int	code 	= UNIT_CODE_UNKNOWN;
	public int	baseUnit 	= UNIT_CODE_UNKNOWN;

	public boolean derived = false;

	public	String	name;
	public	String	abbreviation;

	// Powers of the standard units
	public byte  meter 		= 0;
	public byte  kg 			= 0;
	public byte  sec 			= 0;
	public byte  amper 		= 0;
	public byte  kelvin 		= 0;
	public byte  candela 		= 0;
	public byte  mole 		= 0;
	public byte  radian 		= 0;
	public byte  steradian 	= 0;

	public 	float		koeffA = 1.0f;
	public 	float		koeffB = 0.0f;

	public boolean dimLess = false;
	public boolean doMetricPrefix = false;

	public CCUnit(String name,String abbreviation,boolean derived,int unitCategory,int code,int baseUnit,
	            byte meter,byte kg,byte sec,byte amper,byte kelvin,byte candela,byte mole,byte radian,byte steradian,
	            float koeffA,float koeffB,boolean dimLess,boolean doMetricPrefix){
		this.name 		= name;
		this.abbreviation 	= abbreviation;
		this.derived 		= derived;
		this.unitCategory 	= unitCategory;
		this.code 			= code;
		this.baseUnit 		= baseUnit;
		this.meter		= meter;
		this.kg			= kg;
		this.sec			= sec;
		this.amper 		= amper;
		this.kelvin 		=  kelvin;
		this.candela 		= candela;
		this.mole 			= mole;
		this.radian 		= radian;
		this.steradian 		= steradian;
		this.koeffA		= koeffA;
		this.koeffB		= koeffB;
		this.dimLess		= dimLess;
		this.doMetricPrefix = doMetricPrefix;
		
	} 

	public final static int UNIT_CODE_UNKNOWN			= 0;
	public final static int UNIT_CODE_KG				= 1;
	public final static int UNIT_CODE_G				= 2;
	public final static int UNIT_CODE_MT				= 3;
	public final static int UNIT_CODE_LB				= 4;
	public final static int UNIT_CODE_OZ				= 5;
	public final static int UNIT_CODE_AMU				= 6;
	public final static int UNIT_CODE_METER				= 7;
	public final static int UNIT_CODE_INCH				= 8;
	public final static int UNIT_CODE_YARD				= 9;
	public final static int UNIT_CODE_FEET				= 10;
	public final static int UNIT_CODE_MILE_ST			= 11;
	public final static int UNIT_CODE_MICRON			= 12;
	public final static int UNIT_CODE_S				= 13;
	public final static int UNIT_CODE_MIN				= 14;
	public final static int UNIT_CODE_HOUR				= 15;
	public final static int UNIT_CODE_DAY				= 16;
	public final static int UNIT_CODE_CELSIUS			= 17;
	public final static int UNIT_CODE_KELVIN			= 18;
	public final static int UNIT_CODE_FAHRENHEIT		= 19;
	public final static int UNIT_CODE_M2				= 20;
	public final static int UNIT_CODE_ACRE				= 21;
	public final static int UNIT_CODE_ARE				= 22;
	public final static int UNIT_CODE_HECTARE			= 23;
	public final static int UNIT_CODE_M3				= 24;
	public final static int UNIT_CODE_LITER				= 25;
	public final static int UNIT_CODE_CC				= 26;
	public final static int UNIT_CODE_BBL_D				= 27;
	public final static int UNIT_CODE_BBL_L				= 28;
	public final static int UNIT_CODE_BU				= 29;
	public final static int UNIT_CODE_GAL_D			= 30;
	public final static int UNIT_CODE_GAL_L				= 31;
	public final static int UNIT_CODE_PT_D				= 32;
	public final static int UNIT_CODE_PT_L				= 33;
	public final static int UNIT_CODE_QT_D				= 34;
	public final static int UNIT_CODE_QT_L				= 35;
	public final static int UNIT_CODE_JOULE				= 36;
	public final static int UNIT_CODE_CALORIE			= 37;
	public final static int UNIT_CODE_EV				= 38;
	public final static int UNIT_CODE_ERG				= 39;
	public final static int UNIT_CODE_WHR				= 40;
	public final static int UNIT_CODE_NEWTON			= 41;
	public final static int UNIT_CODE_DYNE				= 42;
	public final static int UNIT_CODE_KGF				= 43;
	public final static int UNIT_CODE_LBF				= 44;
	public final static int UNIT_CODE_WATT				= 45;
	public final static int UNIT_CODE_HP_MECH			= 46;
	public final static int UNIT_CODE_HP_EL				= 47;
	public final static int UNIT_CODE_HP_METR			= 48;
	public final static int UNIT_CODE_PASCAL			= 49;
	public final static int UNIT_CODE_BAR				= 50;
	public final static int UNIT_CODE_ATM				= 51;
	public final static int UNIT_CODE_MMHG				= 52;
	public final static int UNIT_CODE_CMH2O				= 53;
	public final static int UNIT_CODE_TORR				= 54;
	public final static int UNIT_CODE_ANG_VEL			= 55;
	public final static int UNIT_CODE_LINEAR_VEL		= 56;
	public final static int UNIT_CODE_AMPERE			= 57;
	public final static int UNIT_CODE_VOLT				= 58;
	public final static int UNIT_CODE_COULOMB			= 59;
	public final static int UNIT_CODE_MILLIVOLT			= 60;
	public final static int UNIT_CODE_LUMEN				= 61;
	public final static int UNIT_CODE_LUX				= 62;
	public final static int UNIT_TABLE_LENGTH           = 63;

	public final static int UNIT_CAT_UNKNOWN			= 0;
	public final static int UNIT_CAT_LENGTH				= 1;
	public final static int UNIT_CAT_MASS				= 2;
	public final static int UNIT_CAT_TIME				= 3;
	public final static int UNIT_CAT_TEMPERATURE		= 4;
	public final static int UNIT_CAT_AREA				= 5;
	public final static int UNIT_CAT_VOL_CAP			= 6;
	public final static int UNIT_CAT_ENERGY				= 7;
	public final static int UNIT_CAT_FORCE				= 8;
	public final static int UNIT_CAT_POWER				= 9;
	public final static int UNIT_CAT_PRESSURE			= 10;
	public final static int UNIT_CAT_ELECTRICITY		= 11;
	public final static int UNIT_CAT_LIGHT				= 12;
	public final static int UNIT_CAT_MISC				= 13;

	public static String[] catNames = {"Unknown", 
									   "Length",
									   "Mass",
									   "Time",
									   "Temperature",
									   "Area",
									   "Volumes/Capacity",
									   "Energy",
									   "Force",
									   "Power",
									   "Pressure",
									   "Electricity",
									   "Light",
									   "Miscellaneous"};

	public static Vector getCatUnitAbbrev(int index)
	{
		Vector abbrevs = new Vector();
		for(int i = 1; i <= UNIT_CODE_LUX; i++){
			CCUnit u = getUnit(i);
			if(u.unitCategory == index){
				abbrevs.add(u.abbreviation);
			}
		}
		return abbrevs;
	}

	public static CCUnit     getUnit(int code){
		if(code < 0) return null;
		switch(code){
		case UNIT_CODE_KG :
			return new CCUnit("kilogram","kg",false,UNIT_CAT_MASS,UNIT_CODE_KG,UNIT_CODE_KG,
							  (byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1.0f,0.0f,false,false);
		case UNIT_CODE_G :
			return new CCUnit("gram","g",true,UNIT_CAT_MASS,UNIT_CODE_G,UNIT_CODE_KG,
							  (byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.001f,0.0f,false,true); 
		case UNIT_CODE_MT :
			return new CCUnit("metric ton","tn",true,UNIT_CAT_MASS,UNIT_CODE_MT,UNIT_CODE_KG,
							  (byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1000f,0.0f,false,true); 
		case UNIT_CODE_LB : 
			return new CCUnit("pound","lb",true,UNIT_CAT_MASS,UNIT_CODE_LB,UNIT_CODE_KG,
							  (byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.45359237f,0.0f,false,false); 
		case UNIT_CODE_OZ : 
			return new CCUnit("ounce","oz",true,UNIT_CAT_MASS,UNIT_CODE_OZ,UNIT_CODE_G,
							  (byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.028349523f,0.0f,false,false); 
		case UNIT_CODE_AMU : 
			return new CCUnit("atomic mass unit","amu",true,UNIT_CAT_MASS,UNIT_CODE_AMU,UNIT_CODE_KG,
							  (byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1.66054e-27f,0.0f,false,false); 
		case UNIT_CODE_METER : 
			return new CCUnit("meter","m",false,UNIT_CAT_LENGTH,UNIT_CODE_METER,UNIT_CODE_METER,
							  (byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_INCH : 
			return new CCUnit("inch","in",false,UNIT_CAT_LENGTH,UNIT_CODE_INCH,UNIT_CODE_METER,
							  (byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.0254f,0.0f,false,false); 
		case UNIT_CODE_YARD : 
			return new CCUnit("yard","yd",false,UNIT_CAT_LENGTH,UNIT_CODE_YARD,UNIT_CODE_METER,
							  (byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.9144f,0.0f,false,false); 
		case UNIT_CODE_FEET :
			return new CCUnit("feet","ft",false,UNIT_CAT_LENGTH,UNIT_CODE_FEET,UNIT_CODE_METER,
							  (byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.3048f,0.0f,false,false); 
		case UNIT_CODE_MILE_ST :
			return new CCUnit("mile (statute)","mi",false,UNIT_CAT_LENGTH,UNIT_CODE_MILE_ST,UNIT_CODE_METER,
							  (byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1609.344f,0.0f,false,false); 
		case UNIT_CODE_MICRON :
			return new CCUnit("micron","�",false,UNIT_CAT_LENGTH,UNIT_CODE_MICRON,UNIT_CODE_METER,
							  (byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1e-6f,0.0f,false,false); 
		case UNIT_CODE_S : 
			return new CCUnit("second","s",false,UNIT_CAT_TIME,UNIT_CODE_S,UNIT_CODE_S,
							  (byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_MIN :
			return new CCUnit("minute","min",false,UNIT_CAT_TIME,UNIT_CODE_MIN,UNIT_CODE_S,
							  (byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  60f,0.0f,false,false); 
		case UNIT_CODE_HOUR :
			return new CCUnit("hour","hr",false,UNIT_CAT_TIME,UNIT_CODE_HOUR,UNIT_CODE_S,
							  (byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  3600f,0.0f,false,false); 
		case UNIT_CODE_DAY :
			return new CCUnit("day","d",false,UNIT_CAT_TIME,UNIT_CODE_DAY,UNIT_CODE_S,
							  (byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  86400f,0.0f,false,false); 
		case UNIT_CODE_CELSIUS :
			return new CCUnit("Celsius","C",false,UNIT_CAT_TEMPERATURE,UNIT_CODE_CELSIUS,UNIT_CODE_CELSIUS,
							  (byte)0,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,false); 
		case UNIT_CODE_KELVIN :
			return new CCUnit("Kelvin","K",false,UNIT_CAT_TEMPERATURE,UNIT_CODE_KELVIN,UNIT_CODE_CELSIUS,
							  (byte)0,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,-273.15f,false,true); 
		case UNIT_CODE_FAHRENHEIT :
			return new CCUnit("Fahrenheit","F",false,UNIT_CAT_TEMPERATURE,UNIT_CODE_FAHRENHEIT,UNIT_CODE_CELSIUS,
							  (byte)0,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,
							  .55555555556f,-17.777777778f,false,false); 
		case UNIT_CODE_M2 :
			return new CCUnit("m2","m2",false,UNIT_CAT_AREA,UNIT_CODE_M2,UNIT_CODE_M2,
							  (byte)2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,false); 
		case UNIT_CODE_ACRE :
			return new CCUnit("acre","acre",false,UNIT_CAT_AREA,UNIT_CODE_ACRE,UNIT_CODE_M2,
							  (byte)2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  4046.8564f,0.0f,false,false); 
		case UNIT_CODE_ARE :
			return new CCUnit("are","a",false,UNIT_CAT_AREA,UNIT_CODE_ARE,UNIT_CODE_M2,
							  (byte)2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  100f,0.0f,false,false); 
		case UNIT_CODE_HECTARE :
			return new CCUnit("hectare","ha",true,UNIT_CAT_AREA,UNIT_CODE_HECTARE,UNIT_CODE_M2,
							  (byte)2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  10000f,0.0f,false,false); 
		case UNIT_CODE_M3 :
			return new CCUnit("m3","m3",true,UNIT_CAT_VOL_CAP,UNIT_CODE_M3,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,false); 
		case UNIT_CODE_LITER :
			return new CCUnit("liter","L",true,UNIT_CAT_VOL_CAP,UNIT_CODE_LITER,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.001f,0.0f,false,true); 
		case UNIT_CODE_CC :
			return new CCUnit("cc","cc",true,UNIT_CAT_VOL_CAP,UNIT_CODE_CC,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.000001f,0.0f,false,false); 
		case UNIT_CODE_BBL_D :
			return new CCUnit("barrel","bbl",true,UNIT_CAT_VOL_CAP,UNIT_CODE_BBL_D,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.11562712f,0.0f,false,false); 
		case UNIT_CODE_BBL_L :
			return new CCUnit("barrel (l)","bbl",true,UNIT_CAT_VOL_CAP,UNIT_CODE_BBL_L,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.11924047f,0.0f,false,false); 
		case UNIT_CODE_BU :
			return new CCUnit("bushel","bu",true,UNIT_CAT_VOL_CAP,UNIT_CODE_BU,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.03523907f,0.0f,false,false); 
		case UNIT_CODE_GAL_D :
			return new CCUnit("gallon","gal",true,UNIT_CAT_VOL_CAP,UNIT_CODE_GAL_D,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.00440476f,0.0f,false,false); 
		case UNIT_CODE_GAL_L :
			return new CCUnit("gallon (liq)","gal",true,UNIT_CAT_VOL_CAP,UNIT_CODE_GAL_L,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.0037854118f,0.0f,false,false); 
		case UNIT_CODE_PT_D :
			return new CCUnit("pint","pt",true,UNIT_CAT_VOL_CAP,UNIT_CODE_PT_D,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  5.505951e-4f,0.0f,false,false); 
		case UNIT_CODE_PT_L :
			return new CCUnit("pint (liq)","pt",true,UNIT_CAT_VOL_CAP,UNIT_CODE_PT_L,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  4.731632e-4f,0.0f,false,false); 
		case UNIT_CODE_QT_D :
			return new CCUnit("quart","qt",true,UNIT_CAT_VOL_CAP,UNIT_CODE_QT_D,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1.1011901e-3f,0.0f,false,false); 
		case UNIT_CODE_QT_L :
			return new CCUnit("quart (liq)","qt",true,UNIT_CAT_VOL_CAP,UNIT_CODE_QT_L,UNIT_CODE_M3,
							  (byte)3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  9.463264e-4f,0.0f,false,false); 
		case UNIT_CODE_JOULE :
			return new CCUnit("Joule","J",true,UNIT_CAT_ENERGY,UNIT_CODE_JOULE,UNIT_CODE_JOULE,
							  (byte)2,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_CALORIE :
			return new CCUnit("calorie","cal",true,UNIT_CAT_ENERGY,UNIT_CODE_CALORIE,UNIT_CODE_JOULE,
							  (byte)2,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  4.184f,0.0f,false,true); 
		case UNIT_CODE_EV :
			return new CCUnit("eV","eV",true,UNIT_CAT_ENERGY,UNIT_CODE_EV,UNIT_CODE_JOULE,
							  (byte)2,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1.60219e-19f,0.0f,false,true); 
		case UNIT_CODE_ERG :
			return new CCUnit("erg","erg",true,UNIT_CAT_ENERGY,UNIT_CODE_ERG,UNIT_CODE_JOULE,
							  (byte)2,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1e-7f,0.0f,false,true); 
		case UNIT_CODE_WHR :
			return new CCUnit("Watt-hours","Whr",true,UNIT_CAT_ENERGY,UNIT_CODE_WHR,UNIT_CODE_JOULE,
							  (byte)2,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  3600f,0.0f,false,true); 
		case UNIT_CODE_NEWTON :
			return new CCUnit("Newton","N",true,UNIT_CAT_FORCE,UNIT_CODE_NEWTON,UNIT_CODE_NEWTON,
							  (byte)1,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_DYNE :
			return new CCUnit("dyne","dyn",true,UNIT_CAT_FORCE,UNIT_CODE_DYNE,UNIT_CODE_NEWTON,
							  (byte)1,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1e-5f,0.0f,false,true); 
		case UNIT_CODE_KGF :
			return new CCUnit("kilogram-force","kgf",true,UNIT_CAT_FORCE,UNIT_CODE_KGF,UNIT_CODE_NEWTON,
							  (byte)1,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  9.80665f,0.0f,false,false); 
		case UNIT_CODE_LBF :
			return new CCUnit("pound-force","lbf",true,UNIT_CAT_FORCE,UNIT_CODE_LBF,UNIT_CODE_NEWTON,
							  (byte)1,(byte)1,(byte)-2,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  4.448222f,0.0f,false,false); 
		case UNIT_CODE_WATT :
			return new CCUnit("watt","W",true,UNIT_CAT_POWER,UNIT_CODE_WATT,UNIT_CODE_WATT,
							  (byte)2,(byte)1,(byte)-3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_HP_MECH :
			return new CCUnit("horsepower","hp",true,UNIT_CAT_POWER,UNIT_CODE_HP_MECH,UNIT_CODE_WATT,
							  (byte)2,(byte)1,(byte)-3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  745.7f,0.0f,false,false); 
		case UNIT_CODE_HP_EL :
			return new CCUnit("horsepower (el)","hp(el)",true,UNIT_CAT_POWER,UNIT_CODE_HP_EL,UNIT_CODE_WATT,
							  (byte)2,(byte)1,(byte)-3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  746f,0.0f,false,false); 
		case UNIT_CODE_HP_METR :
			return new CCUnit("horsepower (metric)","hp(metric)",true,UNIT_CAT_POWER,UNIT_CODE_HP_METR,UNIT_CODE_WATT,
							  (byte)2,(byte)1,(byte)-3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  735.499f,0.0f,false,false); 
		case UNIT_CODE_PASCAL :
			return new CCUnit("Pascal","Pa",true,UNIT_CAT_PRESSURE,UNIT_CODE_PASCAL,UNIT_CODE_PASCAL,
							  (byte)-1,(byte)1,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_BAR :
			return new CCUnit("bar","bar",true,UNIT_CAT_PRESSURE,UNIT_CODE_BAR,UNIT_CODE_PASCAL,
							  (byte)-1,(byte)1,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1e5f,0.0f,false,true);
		case UNIT_CODE_ATM :
			return new CCUnit("atmosphere","atm",true,UNIT_CAT_PRESSURE,UNIT_CODE_ATM,UNIT_CODE_PASCAL,
							  (byte)-1,(byte)1,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1.01325e5f,0.0f,false,false); 
		case UNIT_CODE_MMHG :
			return new CCUnit("mm Hg","mmHg",true,UNIT_CAT_PRESSURE,UNIT_CODE_MMHG,UNIT_CODE_PASCAL,
							  (byte)-1,(byte)1,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  133.3224f,0.0f,false,false);
		case UNIT_CODE_CMH2O :
			return new CCUnit("cm H2O","cmH2O",true,UNIT_CAT_PRESSURE,UNIT_CODE_CMH2O,UNIT_CODE_PASCAL,
							  (byte)-1,(byte)1,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  98.0638f,0.0f,false,false); 
		case UNIT_CODE_TORR :
			return new CCUnit("torr","torr",true,UNIT_CAT_PRESSURE,UNIT_CODE_TORR,UNIT_CODE_PASCAL,
							  (byte)-1,(byte)1,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  133.3224f,0.0f,false,true); 
		case UNIT_CODE_ANG_VEL :
			return new CCUnit("rad/s","rad/s",true,UNIT_CAT_MISC,UNIT_CODE_ANG_VEL,UNIT_CODE_ANG_VEL,
							  (byte)0,(byte)0,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,
							  1f,0.0f,false,false); 
		case UNIT_CODE_LINEAR_VEL :
			return new CCUnit("m/s","m/s",true,UNIT_CAT_MISC,UNIT_CODE_LINEAR_VEL,UNIT_CODE_LINEAR_VEL,
							  (byte)1,(byte)0,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_AMPERE :
			return new CCUnit("ampere","A",false,UNIT_CAT_ELECTRICITY,UNIT_CODE_AMPERE,UNIT_CODE_AMPERE,
							  (byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_VOLT :
			return new CCUnit("volt","V",true,UNIT_CAT_ELECTRICITY,UNIT_CODE_VOLT,UNIT_CODE_VOLT,
							  (byte)2,(byte)1,(byte)-3,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true); 
		case UNIT_CODE_COULOMB :
			return new CCUnit("coulomb","Q",true,UNIT_CAT_ELECTRICITY,UNIT_CODE_COULOMB,UNIT_CODE_COULOMB,
							  (byte)0,(byte)0,(byte)1,(byte)1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  1f,0.0f,false,true);
		case UNIT_CODE_MILLIVOLT :
			return new CCUnit("millivolt","mV",true,UNIT_CAT_ELECTRICITY,UNIT_CODE_MILLIVOLT,UNIT_CODE_VOLT,
							  (byte)2,(byte)1,(byte)-3,(byte)-1,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.001f,0.0f,false,false); 
		case UNIT_CODE_LUMEN :
			return new CCUnit("lumen","lm",true,UNIT_CAT_POWER,UNIT_CODE_LUMEN,UNIT_CODE_WATT,
							  (byte)2,(byte)1,(byte)-3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.0014641288f,0.0f,false,true); 
		case UNIT_CODE_LUX :
			return new CCUnit("lux","lx",true,UNIT_CAT_LIGHT,UNIT_CODE_LUX,UNIT_CODE_LUX,
							  (byte)0,(byte)1,(byte)-3,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
							  0.0014641288f,0.0f,false,true);
		}

		return null;
	}

	public static int errorConvertStatus = 0;
	public static CCUnit sourceGlobalUnit = null;
	public static CCUnit destGlobalUnit = null;
	
	public static float unitConvert(CCUnit unitSrc, float srcValue,CCUnit unitDest){
		float retValue = srcValue;
		errorConvertStatus = 0;
		if(!isUnitCompatible(unitSrc,unitDest)){
			errorConvertStatus = 1;
			return retValue;
		}
/*
		CCUnit unitSrcBase = CCUnit.getUnit(unitSrc.baseUnit);
		CCUnit unitDestBase = CCUnit.getUnit(unitDest.baseUnit);
		if(unitSrcBase == null || unitDestBase == null || unitSrcBase != unitDestBase){
			errorConvertStatus = 3;
			return retValue;
		}
*/
		retValue = ((srcValue*unitSrc.koeffA + unitSrc.koeffB) - unitDest.koeffB)/ unitDest.koeffA;
		return retValue;
	}
	public static float unitConvert(float srcValue){
		return unitConvert(sourceGlobalUnit,srcValue,destGlobalUnit);
	}
	
	public boolean  setGlobalUnits(int unitIDSrc,int unitIDDest){
		sourceGlobalUnit = destGlobalUnit = null;
		sourceGlobalUnit = CCUnit.getUnit(unitIDSrc);
		if(sourceGlobalUnit == null) return false;
		destGlobalUnit = CCUnit.getUnit(unitIDDest);
		if(destGlobalUnit == null) return false;
		return true;
	}
	
	public static boolean isUnitCompatible(CCUnit unitSrc,CCUnit unitDest){
		if(unitSrc == null || unitDest == null) return false;
		return ((unitSrc.meter == unitDest.meter) &&
		    (unitSrc.kg == unitDest.kg) &&
		    (unitSrc.sec == unitDest.sec) &&
		    (unitSrc.amper == unitDest.amper) &&
		    (unitSrc.kelvin == unitDest.kelvin) &&
		    (unitSrc.candela == unitDest.candela) &&
		    (unitSrc.mole == unitDest.mole) &&
		    (unitSrc.radian == unitDest.radian) &&
		    (unitSrc.steradian == unitDest.steradian));
	}
	public static boolean isUnitCompatible(int unitIDSrc,int unitIDDest){
		return isUnitCompatible(CCUnit.getUnit(unitIDSrc),CCUnit.getUnit(unitIDDest));
	}
	
	public static float unitConvert(int unitIDSrc, float srcValue,int unitIDDest){
		return unitConvert(CCUnit.getUnit(unitIDSrc),srcValue,CCUnit.getUnit(unitIDDest));
	}
	public static String getPrefixStringForUnit(CCUnit p,int order){
		String retValue = null;
		if(p == null || !p.doMetricPrefix) return retValue;
		switch(order){
			case -12: 	retValue = "p"; break;
			case -9:	retValue = "n"; break;
			case -6:	retValue = "�"; break;
			case -3:	retValue = "m"; break;
			case -2:	retValue = "c"; break;
			case -1:	retValue = "d"; break;
			case 3:	retValue = "k"; break;
			case 6:	retValue = "M"; break;
			case 9:	retValue = "G"; break;
		}
		return retValue;
	}
	public static float getPrefixKoeffForUnit(CCUnit p,int order){
		float retValue = -1f;
		if(p == null || !p.doMetricPrefix) return retValue;
		switch(order){
			case -12: 	retValue = 1e-12f; break;
			case -9:	retValue = 1e-9f; break;
			case -6:	retValue = 1e-6f; break;
			case -3:	retValue = 1e-3f; break;
			case -2:	retValue = 0.01f; break;
			case -1:	retValue = 0.1f; break;
			case 3:	retValue = 1000f; break;
			case 6:	retValue = 1e6f; break;
			case 9:	retValue = 1e9f; break;
		}
		return retValue;
	}
}
