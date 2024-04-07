package com.generator.acm;

import java.util.Scanner;

/**
* ACMÊäÈëÄ£°å
* @author mm
*/
public class MainTempalte {
public static void main(String[] args) {
Scanner scanner = new Scanner(System.in);

    while(scanner.hasNext()){
        int n = scanner.nextInt();

        int[] arr = new int[n];
        for(int i = 0; i < n; i++){
        arr[i] = scanner.nextInt();
        }

        int sum = 0;
        for(int num : arr){
        sum += num;
        }

System.out.println("diadida" + sum);
    }


scanner.close();
}
}