package extra.util;



public class CCUnit

{



public int unitCategory  = UNIT_CAT_UNKNOWN;



public int	code 	= UNIT_CODE_UNKNOWN;

public int	baseUnit 	= UNIT_CODE_UNKNOWN;



public boolean derived = false;



public	String	name;

public	String	abbreviation;



public 	float		koeffA = 1.0f;

public 	float		koeffB = 0.0f;



public int  meter 		= 0;

public int  kg 			= 0;

public int  sec 		= 0;

public int  amper 		= 0;

public int  kelvin 		= 0;

public int  candela 		= 0;

public int  mole 		= 0;

public int  radian 		= 0;

public int  steradian 	= 0;





	public CCUnit(String name,String abbreviation,boolean derived,int unitCategory,int code,int baseUnit,

	            int meter,int kg,int sec,int amper,int kelvin,int candela,int mole,int radian,int steradian,

	            float koeffA,float koeffB){

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

		

	} 





	public static CCUnit     getUnit(int code){

		if(code < 0) return null;

		for(int i = 0; i < unitTable.length;i++){

			if(unitTable[i].code == code){

				return unitTable[i];

			}

		}

		return null;

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

public final static int UNIT_CODE_ACRE				= 20;

public final static int UNIT_CODE_ARE				= 21;

public final static int UNIT_CODE_HECTARE			= 22;

public final static int UNIT_CODE_LITER				= 23;

public final static int UNIT_CODE_CC				= 24;

public final static int UNIT_CODE_BBL_D				= 25;

public final static int UNIT_CODE_BBL_L				= 26;

public final static int UNIT_CODE_BU				= 27;

public final static int UNIT_CODE_GAL_D			= 28;

public final static int UNIT_CODE_GAL_L				= 29;

public final static int UNIT_CODE_PT_D				= 30;

public final static int UNIT_CODE_PT_L				= 31;

public final static int UNIT_CODE_QT_D				= 32;

public final static int UNIT_CODE_QT_L				= 33;

public final static int UNIT_CODE_JOULE				= 34;

public final static int UNIT_CODE_CALORIE			= 35;

public final static int UNIT_CODE_EV				= 36;

public final static int UNIT_CODE_ERG				= 37;

public final static int UNIT_CODE_WHR				= 38;

public final static int UNIT_CODE_NEWTON			= 39;

public final static int UNIT_CODE_DYNE				= 40;

public final static int UNIT_CODE_WATT				= 41;

public final static int UNIT_CODE_HP_MECH			= 42;

public final static int UNIT_CODE_HP_EL				= 43;

public final static int UNIT_CODE_HP_METR			= 44;

public final static int UNIT_CODE_PASCAL			= 45;

public final static int UNIT_CODE_BAR				= 46;

public final static int UNIT_CODE_ATM				= 47;

public final static int UNIT_CODE_MMHG				= 48;

public final static int UNIT_CODE_CMH2O			= 49;

public final static int UNIT_CODE_TORR				= 50;



public final static int UNIT_CAT_UNKNOWN			= 0;

public final static int UNIT_CAT_LENGTH			= 1;

public final static int UNIT_CAT_MASS				= 2;

public final static int UNIT_CAT_TIME				= 3;

public final static int UNIT_CAT_TEMPERATURE		= 4;

public final static int UNIT_CAT_AREA				= 5;

public final static int UNIT_CAT_VOL_CAP			= 6;

public final static int UNIT_CAT_ENERGY				= 7;

public final static int UNIT_CAT_FORCE				= 8;

public final static int UNIT_CAT_POWER				= 9;

public final static int UNIT_CAT_PRESSURE			= 10;





private  static CCUnit 	[]unitTable = 

	{

		new CCUnit("kilogram","kg",false,UNIT_CAT_MASS,UNIT_CODE_KG,UNIT_CODE_KG,0,1,0,0,0,0,0,0,0,1.0f,0.0f),

		new CCUnit("gram","g",true,UNIT_CAT_MASS,UNIT_CODE_G,UNIT_CODE_KG,0,1,0,0,0,0,0,0,0,0.001f,0.0f),

		new CCUnit("metric ton","tn",true,UNIT_CAT_MASS,UNIT_CODE_MT,UNIT_CODE_KG,0,1,0,0,0,0,0,0,0,1000f,0.0f),

		new CCUnit("pound","lb",true,UNIT_CAT_MASS,UNIT_CODE_LB,UNIT_CODE_KG,0,1,0,0,0,0,0,0,0,0.45359237f,0.0f),

		new CCUnit("ounce","oz",true,UNIT_CAT_MASS,UNIT_CODE_OZ,UNIT_CODE_G,0,1,0,0,0,0,0,0,0,0.028349523f,0.0f),

		new CCUnit("atomic mass unit","amu",true,UNIT_CAT_LENGTH,UNIT_CODE_AMU,UNIT_CODE_KG,0,1,0,0,0,0,0,0,0,1.66054e-27f,0.0f),

		new CCUnit("meter","m",false,UNIT_CAT_LENGTH,UNIT_CODE_METER,UNIT_CODE_METER,1,0,0,0,0,0,0,0,0,1f,0.0f),

		new CCUnit("inch","in",false,UNIT_CAT_LENGTH,UNIT_CODE_INCH,UNIT_CODE_METER,1,0,0,0,0,0,0,0,0,0.0254f,0.0f),

		new CCUnit("yard","yd",false,UNIT_CAT_LENGTH,UNIT_CODE_YARD,UNIT_CODE_METER,1,0,0,0,0,0,0,0,0,0.9144f,0.0f),

		new CCUnit("feet","ft",false,UNIT_CAT_LENGTH,UNIT_CODE_FEET,UNIT_CODE_METER,1,0,0,0,0,0,0,0,0,0.3048f,0.0f),

		new CCUnit("mile (statute)","mi",false,UNIT_CAT_LENGTH,UNIT_CODE_MILE_ST,UNIT_CODE_METER,1,0,0,0,0,0,0,0,0,1609.344f,0.0f),

		new CCUnit("micron","µ",false,UNIT_CAT_LENGTH,UNIT_CODE_MICRON,UNIT_CODE_METER,1,0,0,0,0,0,0,0,0,1e-6f,0.0f),

		new CCUnit("second","s",false,UNIT_CAT_TIME,UNIT_CODE_S,UNIT_CODE_S,0,0,1,0,0,0,0,0,0,1f,0.0f),

		new CCUnit("minute","min",false,UNIT_CAT_TIME,UNIT_CODE_MIN,UNIT_CODE_S,0,0,1,0,0,0,0,0,0,60f,0.0f),

		new CCUnit("hour","hr",false,UNIT_CAT_TIME,UNIT_CODE_HOUR,UNIT_CODE_S,0,0,1,0,0,0,0,0,0,3600f,0.0f),

		new CCUnit("day","d",false,UNIT_CAT_TIME,UNIT_CODE_DAY,UNIT_CODE_S,0,0,1,0,0,0,0,0,0,86400f,0.0f),

		new CCUnit("Celsius","C",false,UNIT_CAT_TEMPERATURE,UNIT_CODE_CELSIUS,UNIT_CODE_CELSIUS,0,0,0,0,1,0,0,0,0,1f,0.0f),

		new CCUnit("Kelvin","K",false,UNIT_CAT_TEMPERATURE,UNIT_CODE_KELVIN,UNIT_CODE_CELSIUS,0,0,0,0,1,0,0,0,0,1f,273.15f),

		new CCUnit("Fahrenheit","F",false,UNIT_CAT_TEMPERATURE,UNIT_CODE_FAHRENHEIT,UNIT_CODE_CELSIUS,0,0,0,0,1,0,0,0,0,1.8f,32f),

		new CCUnit("acre","acre",false,UNIT_CAT_AREA,UNIT_CODE_ACRE,UNIT_CODE_METER,2,0,0,0,0,0,0,0,0,4046.8564f,0.0f),

		new CCUnit("are","a",false,UNIT_CAT_AREA,UNIT_CODE_ARE,UNIT_CODE_METER,2,0,0,0,0,0,0,0,0,100f,0.0f),

		new CCUnit("hectare","ha",true,UNIT_CAT_AREA,UNIT_CODE_HECTARE,UNIT_CODE_METER,2,0,0,0,0,0,0,0,0,10000f,0.0f),

		new CCUnit("liter","L",true,UNIT_CAT_VOL_CAP,UNIT_CODE_LITER,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,0.001f,0.0f),

		new CCUnit("cc","cc",true,UNIT_CAT_VOL_CAP,UNIT_CODE_CC,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,0.000001f,0.0f),

		new CCUnit("barrel","bbl",true,UNIT_CAT_VOL_CAP,UNIT_CODE_BBL_D,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,0.11562712f,0.0f),

		new CCUnit("barrel (l)","bbl",true,UNIT_CAT_VOL_CAP,UNIT_CODE_BBL_L,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,0.11924047f,0.0f),

		new CCUnit("bushel","bu",true,UNIT_CAT_VOL_CAP,UNIT_CODE_BU,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,0.03523907f,0.0f),

		new CCUnit("gallon","gal",true,UNIT_CAT_VOL_CAP,UNIT_CODE_GAL_D,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,0.00440476f,0.0f),

		new CCUnit("gallon (liq)","gal",true,UNIT_CAT_VOL_CAP,UNIT_CODE_GAL_L,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,0.0037854118f,0.0f),

		new CCUnit("pint","pt",true,UNIT_CAT_VOL_CAP,UNIT_CODE_PT_D,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,5.505951e-4f,0.0f),

		new CCUnit("pint (liq)","pt",true,UNIT_CAT_VOL_CAP,UNIT_CODE_PT_L,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,4.731632e-4f,0.0f),

		new CCUnit("quart","qt",true,UNIT_CAT_VOL_CAP,UNIT_CODE_QT_D,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,1.1011901e-3f,0.0f),

		new CCUnit("quart (liq)","qt",true,UNIT_CAT_VOL_CAP,UNIT_CODE_QT_L,UNIT_CODE_METER,3,0,0,0,0,0,0,0,0,9.463264e-4f,0.0f),

		new CCUnit("Joule","J",true,UNIT_CAT_ENERGY,UNIT_CODE_JOULE,UNIT_CODE_JOULE,2,1,-2,0,0,0,0,0,0,1f,0.0f),

		new CCUnit("calorie","cal",true,UNIT_CAT_ENERGY,UNIT_CODE_CALORIE,UNIT_CODE_JOULE,2,1,-2,0,0,0,0,0,0,4.184f,0.0f),

		new CCUnit("eV","eV",true,UNIT_CAT_ENERGY,UNIT_CODE_EV,UNIT_CODE_JOULE,2,1,-2,0,0,0,0,0,0,1.60219e-19f,0.0f),

		new CCUnit("erg","erg",true,UNIT_CAT_ENERGY,UNIT_CODE_ERG,UNIT_CODE_JOULE,2,1,-2,0,0,0,0,0,0,1e-7f,0.0f),

		new CCUnit("Watt-hours","Whr",true,UNIT_CAT_ENERGY,UNIT_CODE_WHR,UNIT_CODE_JOULE,2,1,-2,0,0,0,0,0,0,3600f,0.0f),

		new CCUnit("Newton","N",true,UNIT_CAT_FORCE,UNIT_CODE_NEWTON,UNIT_CODE_NEWTON,1,1,-2,0,0,0,0,0,0,1f,0.0f),

		new CCUnit("dyne","dyn",true,UNIT_CAT_FORCE,UNIT_CODE_DYNE,UNIT_CODE_NEWTON,1,1,-2,0,0,0,0,0,0,1e-5f,0.0f),

		new CCUnit("watt","W",true,UNIT_CAT_POWER,UNIT_CODE_WATT,UNIT_CODE_WATT,2,1,-3,0,0,0,0,0,0,1f,0.0f),

		new CCUnit("horsepower","hp",true,UNIT_CAT_POWER,UNIT_CODE_HP_MECH,UNIT_CODE_WATT,2,1,-3,0,0,0,0,0,0,745.7f,0.0f),

		new CCUnit("horsepower (el)","hp",true,UNIT_CAT_POWER,UNIT_CODE_HP_EL,UNIT_CODE_WATT,2,1,-3,0,0,0,0,0,0,746f,0.0f),

		new CCUnit("horsepower (metric)","hp",true,UNIT_CAT_POWER,UNIT_CODE_HP_METR,UNIT_CODE_WATT,2,1,-3,0,0,0,0,0,0,735.499f,0.0f),



		new CCUnit("Pascal","Pa",true,UNIT_CAT_PRESSURE,UNIT_CODE_PASCAL,UNIT_CODE_PASCAL,-1,1,-1,0,0,0,0,0,0,1f,0.0f),

		new CCUnit("bar","bar",true,UNIT_CAT_PRESSURE,UNIT_CODE_BAR,UNIT_CODE_PASCAL,-1,1,-1,0,0,0,0,0,0,1e5f,0.0f),

		new CCUnit("atmosphere","atm",true,UNIT_CAT_PRESSURE,UNIT_CODE_ATM,UNIT_CODE_PASCAL,-1,1,-1,0,0,0,0,0,0,1.01325e5f,0.0f),

		new CCUnit("mm Hg","mmHg",true,UNIT_CAT_PRESSURE,UNIT_CODE_MMHG,UNIT_CODE_PASCAL,-1,1,-1,0,0,0,0,0,0,133.3224f,0.0f),

		new CCUnit("cm H2O","cmH2O",true,UNIT_CAT_PRESSURE,UNIT_CODE_CMH2O,UNIT_CODE_PASCAL,-1,1,-1,0,0,0,0,0,0,98.0638f,0.0f),

		new CCUnit("torr","torr",true,UNIT_CAT_PRESSURE,UNIT_CODE_TORR,UNIT_CODE_PASCAL,-1,1,-1,0,0,0,0,0,0,133.3224f,0.0f),

	};



}