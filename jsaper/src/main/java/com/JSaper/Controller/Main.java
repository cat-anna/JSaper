package com.JSaper.Controller;

import com.Multisaper.Core.Interfaces.Controller;


public class Main {
	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
				try {
					Controller bc = new PCController();
					bc.BeginGame();
				} catch (Exception e) {
					e.printStackTrace();
				}
//			}
//		});
	}
}


/*

czas up�ywa nadal po zako�czeniu gry


*/