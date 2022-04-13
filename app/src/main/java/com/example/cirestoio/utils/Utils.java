package com.example.cirestoio.utils;

public class Utils {

    public static Double getBanknoteDoubleVal(String label) {
        switch (label) {
            case "5euro":
                return 5.00;
            case "10euro":
                return 10.00;
            case "20euro":
                return 20.00;
            case "50euro":
                return 50.00;
            case "100euro":
                return 100.00;
            default:
                return 0.00;
        }
    }

    public static int getBanknoteIndex(String label) {
        switch (label) {
            case "5euro":
                return 0;
            case "10euro":
                return 1;
            case "20euro":
                return 2;
            case "50euro":
                return 3;
            case "100euro":
                return 4;
            default:
                return -1;
        }
    }

    public static CharSequence getCorrectString(int num){
        if(num == 0){
            return "Non è stata rilevata alcuna banconota";
        }
        else if (num == 1){
            return "è stata rilevata una banconota";
        }
        else {
            return "Sono state rilevate "+num+" banconote";
        }

    }

    public static CharSequence getCorrectString(CharSequence str) {
        return str.toString().replaceAll("\\s1\\s", "una");
    }

}
