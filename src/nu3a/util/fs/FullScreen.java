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

package nu3a.util.fs;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.StringTokenizer;

import nu3a.render.N3Render;
import nu3a.util.timer.N3Timer;
import nu3a.util.timer.N3TimerListener;

/**
 * Esta clase permite renderizar a pantalla completa. Se debe heredar de esta
 * clase si se quiere hacer render a pantalla completa.
 */
public abstract class FullScreen implements N3TimerListener {
	private GraphicsEnvironment env;
	private GraphicsDevice graphicsDevice;
	private DisplayMode oldDisplayMode;
	/**
	 * Es el componente donde se realiza el render.
	 */
	protected Frame window;

	/**
	 * Ancho en pixels de la pantalla de render.
	 */
	protected int width;
	/**
	 * Alto en pixels de la pantalla de render.
	 */
	protected int height;

	/**
	 * Temporizador para controlar los FPS.
	 */
	protected N3Timer timer;

	/**
	 * Referencia a la implementacion de render que se utilizara.
	 */
	protected N3Render render;

	/**
	 * Constructor de la clase.
	 * 
	 * @param width
	 *            Ancho en pixels de la pantalla de render.
	 * @param height
	 *            Alto en pixels de la pantalla de render.
	 * @param depth
	 *            Profundidad de color a utilizar.
	 */
	public FullScreen(int width, int height, int depth) {
		init();
		initFullScreen(width, height, depth, 0);
	}

	/**
	 * Constructor de la clase.
	 * 
	 * @param width
	 *            Ancho en pixels de la pantalla de render.
	 * @param height
	 *            Alto en pixels de la pantalla de render.
	 * @param depth
	 *            Profundidad de color a utilizar.
	 * @param refresh
	 *            Frecuencia de refresco medida en Hz.
	 */
	public FullScreen(int width, int height, int depth, int refresh) {
		init();
		initFullScreen(width, height, depth, refresh);
	}

	/**
	 * Constructor de la clase.
	 * 
	 * @param screen
	 *            Caracteristicas de la pantalla de render de la forma
	 *            widthXheightXdepth
	 */

	public FullScreen(String screen) {
		init();
		StringTokenizer strTok = new StringTokenizer(screen, "X");
		int w = Integer.parseInt(strTok.nextToken());
		int h = Integer.parseInt(strTok.nextToken());
		int d = Integer.parseInt(strTok.nextToken());
		initFullScreen(w, h, d, 0);
	}

	/**
	 * Realiza la inicializacion previa del modo pantalla completa.
	 */
	private void init() {
		env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsDevice = env.getDefaultScreenDevice();
		oldDisplayMode = graphicsDevice.getDisplayMode();
		if (!graphicsDevice.isFullScreenSupported()) {
			System.out.println("Full screen mode failed");
			System.exit(1);
		} else {
			GraphicsConfiguration gc = graphicsDevice.getDefaultConfiguration();
			window = new Frame(gc);
		}
	}

	/**
	 * Pasa a modo grafico en pantalla completa.
	 */
	private void initFullScreen(int width, int height, int depth, int refresh) {
		this.width = width;
		this.height = height;
		window.setUndecorated(true);
		window.setIgnoreRepaint(true);
		try {
			graphicsDevice.setFullScreenWindow(window);
			graphicsDevice.setDisplayMode(new DisplayMode(width, height, depth,
					refresh));
			timer = new N3Timer(1, this);

		} catch (Exception e) {
			shutdown();
		}
	}

	/**
	 * Permite volver al modo grafico antiguo.
	 */
	protected void shutdown() {
		graphicsDevice.setDisplayMode(oldDisplayMode);
		graphicsDevice.setFullScreenWindow(null);
	}

	/**
	 * Inicia el timer que controla los FPS.
	 */
	public void start() {
		timer.start();
	}

	/**
	 * Permite cambiar los FPS (Frames Per Second).
	 * 
	 * @param fps
	 *            Frames por segundo.
	 */
	public void setFPS(int fps) {
		timer.setTime(1 / fps);
	}

	/**
	 * Aqui se realiza el render en pantalla completa. Debe ser implementado en
	 * las clases hijas.
	 */
	abstract public void display();
}