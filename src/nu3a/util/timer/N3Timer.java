/*
 *	Copyright (c) 2003 Jorge García, Unai Aguilera
 *
 *	This file is part of Nu3A.
 *
 *   Nu3A is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   Nu3A is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Nu3A.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *	Authors: Jorge García <bardok@gmail.com>, Unai Aguilera <gkalgan@gmail.com>
 */

package nu3a.util.timer;

/**
 * Clase que implementa la fincionalidad de un temporizador.
 */
public class N3Timer extends Thread {
	int time;
	N3TimerListener t;

	/**
	 * Crea una instancia de la clase.
	 * 
	 * @param time
	 *            Tiempo, en milisegundos, que pasará entre cada vuelta del
	 *            temporizador
	 * @param t
	 *            Objeto al que se notificará una vez expirado el tiempo
	 */
	public N3Timer(int time, N3TimerListener t) {
		this.time = time;
		this.t = t;
	}

	/**
	 * Establece el retardo del temporizador.
	 * 
	 * @param time
	 *            Retardo
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * Cuerpo de la ejecución.
	 */
	public void run() {
		while (true) {
			try {
				sleep(time);
			} catch (InterruptedException ie) {
				System.out.println("Interrumpido, malo, malo");
			}
			t.idle();
		}
	}
}
