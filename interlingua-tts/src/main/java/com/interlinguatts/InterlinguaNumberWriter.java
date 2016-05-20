package com.interlinguatts;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InterlinguaNumberWriter {
    private static final BigInteger THOUSAND = BigInteger.valueOf(1000);
    //http://members.optus.net/~ado_hall/interlingua/gi/parts/num.html
    private String[] unitNames = {"zero", "un", "duo", "tres", "quatro", "cinque", "sex", "septe", "octo", "nove"};
    private String[] ordinalUnitNames = {"zeresime", "prime", "secunde", "tertie", "quarte", "quinte", "sexte", "septime", "octave", "none"};
    private String[] tenNames = {"", "dece", "vinti", "trenta", "quaranta", "cinquanta", "sexanta", "septanta", "octanta", "novanta"};

    private String[] powersOfThousandNames = {
            null,
            "milles",

            "milliones",
            "milliardos",

            "billiones",
            "billiardos",

            "trilliones",
            "trilliardos",

            "quatrilliones",
            "quatrilliardos",

            "quintilliones",
            "quintilliardos",

            "sextilliones",
            "sextilliardos",

            "septilliones",
            "septilliardos",

            "octilliones",
            "octilliardos",

            "nonilliones",
            "nonilliardos",

            "decilliones",
            "decilliardos"
    };

    private String singular(String wordName) {
        if(wordName == null) {
            return null;
        }

        if(!wordName.endsWith("s")) {
            return wordName;
        }

        if(wordName.equals("milles")) {
            return "mille";
        }

        if(wordName.endsWith("es")) {
            return wordName.substring(0, wordName.length() - 2);
        }


        return wordName.substring(0, wordName.length() - 1);

    }

    //0 - 9
    public String writeUnits (int value) {
        return unitNames[value];
    }

    public String writeOrdinalUnits (int value) {
        return ordinalUnitNames[value];
    }


    //0 - 99
    public String writeTens (int value) {
        if(value < 10) {
            return writeUnits(value);
        }

        int units = value % 10;
        int tens = value / 10;

        String tensName = tenNames[tens];
        return (units == 0)? tensName : tensName + "-" + writeUnits(units);
    }

    public String lastOrdinalWord(String cardinal) {
        int unit = Lists.newArrayList(unitNames).indexOf(cardinal);
        if(unit >=0) {
            return ordinalUnitNames[unit];
        }

        if(cardinal.equals("dece")) {
            return "decime";
        }

        String stem = singular(cardinal);
        if(stem.matches(".*[aeiou]$")) {
            stem = stem.substring(0, stem.length()-1);
        }
        return stem + "esime";
    }

    public String writeOrdinalTens (int value) {
        if(value < 10) {
            return writeOrdinalUnits(value);
        }

        int units = value % 10;
        int tens = value / 10;

        String tensName = tenNames[tens];
        return (units == 0)? lastOrdinalWord(tensName) : tensName + "-" + writeOrdinalUnits(units);
    }

    //0 - 999
    public String writeHundreds (int value) {
        if(value < 100) {
            return writeTens(value);
        }

        int tens = value % 100;
        int hundreds = value / 100;
        String hundredsName = (hundreds == 1)? "cento" : writeUnits(hundreds) + " centos";

        return (tens == 0)? hundredsName : hundredsName + " " + writeTens(tens);
    }

    public String writeOrdinalHundreds (int value) {
        if(value < 100) {
            return writeOrdinalTens(value);
        }

        int tens = value % 100;
        int hundreds = value / 100;
        String hundredsName = (hundreds == 1)? "cento" : writeUnits(hundreds) + " centos";

        return (tens == 0)? lastOrdinalWord(hundredsName) : hundredsName + " " + writeOrdinalTens(tens);
    }

    public String write(int value) {
        return write(BigInteger.valueOf(value));
    }

    public String writeDigitwise(BigInteger value) {
        return writeDigitwise(value.toString());
    }

    public String writeDigitwise(String integerValue) {
        List<String> name = new LinkedList<String>();
        for(int i=0; i<integerValue.length(); i++) {
            name.add(writeUnits(Integer.valueOf("" + integerValue.charAt(i))));
        }
        return Joiner.on(" ").skipNulls().join(name);
    }



    public String write(BigInteger value) {
        if(value.compareTo(THOUSAND) == -1) { // value < 1000
            return writeHundreds(value.intValue());
        }

        BigInteger remainder = value;
        List<String> name = new ArrayList<String>();
        // highest power down to 1.000.0000
        for(int i= powersOfThousandNames.length-1; i >= 0; i--) {
            //0 - 999
            BigInteger powerOfThousand = THOUSAND.pow(i);
            BigInteger unitsOfThisPower = remainder.divide(powerOfThousand);

            remainder = remainder.remainder(powerOfThousand);
            if(unitsOfThisPower.equals(BigInteger.ZERO)) {
                continue;
            }

            //Units of tbis power
            String unitsOfThisPowerName;
            if(unitsOfThisPower.compareTo(THOUSAND) >= 0) { // unitsOfThisPower >= 1000
                unitsOfThisPowerName = write(unitsOfThisPower);
                if(needsDe(unitsOfThisPowerName))
                    unitsOfThisPowerName += " de";
            } else {
                unitsOfThisPowerName = writeHundreds(unitsOfThisPower.intValue());
            }

            //Units of tbis power + power name
            String thisPowerName = powersOfThousandNames[i];
            if(unitsOfThisPower.equals(BigInteger.ONE)) {
                if(i == 1) { //mille
                    name.add(singular(thisPowerName)); //ommit 'un' in mille
                } else {
                    name.add(unitsOfThisPowerName);
                    name.add(singular(thisPowerName));
                }
            } else {
                name.add(unitsOfThisPowerName);
                name.add(thisPowerName);
            }
        }

        return Joiner.on(" ").skipNulls().join(name);
    }


    public String write (BigDecimal value) {

        if(value.scale() == 0) {
            return write(value.toBigInteger());
        }

        BigDecimal[] parts = value.divideAndRemainder(BigDecimal.ONE);

        BigDecimal integerPart = parts[0];
        String integerPartName = write(integerPart.toBigInteger());

        BigDecimal fractionalPart = parts[1];
        return integerPartName + " comma " + writeDigitwise(fractionalPart.toString().substring(2));
    }

    private boolean needsDe(String unitsOfThisPowerName) {
        return unitsOfThisPowerName.endsWith("llion") ||
                unitsOfThisPowerName.endsWith("lliones") ||
                unitsOfThisPowerName.endsWith("lliardo") ||
                unitsOfThisPowerName.endsWith("lliardos");
    }

    public String writeOrdinal(BigInteger value) {
        if(value.compareTo(THOUSAND) == -1) { // value < 1000
            return writeOrdinalHundreds(value.intValue());
        }

        BigInteger remainder = value;
        List<String> name = new ArrayList<String>();
        // highest power down to 1.000.0000

        int i= powersOfThousandNames.length-1;
        BigInteger unitsOfThisPower = null;
        for(; i >= 0; i--) {
            //0 - 999
            BigInteger powerOfThousand = THOUSAND.pow(i);
            unitsOfThisPower = remainder.divide(powerOfThousand);

            remainder = remainder.remainder(powerOfThousand);
            if(unitsOfThisPower.equals(BigInteger.ZERO)) {
                continue;
            }

            //Units of tbis power
            String unitsOfThisPowerName;
            if(unitsOfThisPower.compareTo(THOUSAND) >= 0) { // unitsOfThisPower >= 1000
                unitsOfThisPowerName = write(unitsOfThisPower);
                if(needsDe(unitsOfThisPowerName))
                    unitsOfThisPowerName += " de";
            } else {
                unitsOfThisPowerName = writeHundreds(unitsOfThisPower.intValue());
            }

            //Units of tbis power + power name
            String thisPowerName = powersOfThousandNames[i];


            if(remainder.compareTo(BigInteger.ZERO) > 0) { //cardinal
                if(unitsOfThisPower.equals(BigInteger.ONE)) {
                    if(i == 1) { //mille
                        name.add(singular(thisPowerName)); //ommit 'un' in mille
                    } else {
                        name.add(unitsOfThisPowerName + " " + singular(thisPowerName));
                    }
                } else {
                    name.add(unitsOfThisPowerName + " " + thisPowerName);
                }
            } else { //ordinal
                if(i==0) {
                    name.add(writeOrdinalHundreds(unitsOfThisPower.intValue()));
                } else {
                    String ordinalName = lastOrdinalWord(thisPowerName);
                    if(unitsOfThisPower.equals(BigInteger.ONE)) {
                        name.add(ordinalName);
                    } else {
                        name.add(unitsOfThisPowerName + " " + ordinalName);
                    }
                }
            }
        }

        return Joiner.on(" ").join(name);
    }

    public String writeOrdinalAdverb(BigInteger value) {
        String ordinal = writeOrdinal(value);
        return ordinal.substring(0, ordinal.length()-1) + "o";
    }

    public static void main(String[] args) {
        InterlinguaNumberWriter writer = new InterlinguaNumberWriter();
        for(int i=0; i<1000; i++) {
            System.out.println(writer.writeOrdinal(BigInteger.TEN.pow(i).add(BigInteger.ONE)));
        }
        //36456465416540000l
        System.out.println(writer.writeOrdinalAdverb(new BigInteger("3645646540001000000")));

        System.out.println(writer.write(new BigDecimal("87.005")));
        System.out.println(new BigDecimal("87.005"));
        //System.out.println(writer.write(new BigInteger("1,002,030,400,000,000,000,000".replaceAll(",", ""))));
    }

}
