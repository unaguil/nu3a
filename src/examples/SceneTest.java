/*
*	Copyright (c) 2003, 2012 Jorge García, Unai Aguilera
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

package examples;

import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

import javax.swing.JFrame;

import nu3a.collision.N3Ray;
import nu3a.geometry.N3GeometryData;
import nu3a.geometry.N3Point2D;
import nu3a.light.N3LightData;
import nu3a.material.N3Material;
import nu3a.material.color.N3ColorRGBA;
import nu3a.material.texture.N3Texture2D;
import nu3a.math.N3Vector3D;
import nu3a.render.N3Render;
import nu3a.render.software.N3SoftwareRender;
import nu3a.scene.N3Camera;
import nu3a.scene.N3GroupNode;
import nu3a.scene.N3Light;
import nu3a.scene.N3Scene;
import nu3a.scene.N3TransformationNode;
import nu3a.scene.N3VisualMesh;
import nu3a.scene.N3VisualObject;
import nu3a.util.imageLoader.N3JDKImageLoader;
import nu3a.util.timer.N3Timer;
import nu3a.util.timer.N3TimerListener;

public class SceneTest extends JFrame implements WindowListener, KeyListener, MouseListener, N3TimerListener {
	Panel c;
	N3Material material;
	N3VisualMesh suelo;
	N3Scene scene = new N3Scene();
	N3Render render;
	N3Timer t;

	N3GroupNode grpScene;
	N3VisualMesh sphere;
	N3TransformationNode cubeTrans;
	N3TransformationNode sphereTrans;
	N3TransformationNode camera1Trans;
	N3TransformationNode light01Trans;

	N3VisualMesh cube, cube2;

	boolean cullFacing = false;
	boolean light = true;
	N3Light light01;

	boolean cameraTransform = false;

	float spotAngle = 20.0f;
	boolean soft = false;

	public SceneTest() {
		super("Scene Test");
		setSize(320, 240);
		addWindowListener(this);
		init();
		start();
	}

	public void init() {
		c = new Panel();
		this.getContentPane().add(c);
		setVisible(true);
	}

	void loadMaterials() throws Exception {
		material = new N3Material(scene, "mat1");

		InputStream is = new FileInputStream("files/nu3a.png");

		N3JDKImageLoader loader = new N3JDKImageLoader(is);
		N3Texture2D texture = new N3Texture2D(scene, "textura1");
		texture.genTexture(loader, render);
		material.addTexture(texture);
		material.setAmbientColor(new N3ColorRGBA(1, 1, 1));
		material.setDiffuseColor(new N3ColorRGBA(1, 1, 1));
		material.setSpecularColor(new N3ColorRGBA(0.2f, 0.2f, 0.2f), 0.5f);
		material.applyMaterial(true);
	}

	void createScene() throws Exception {

		/*
		 * Ra�z | ---------------------------------------------------- | | |
		 * grpScene | | ----------------------- camera1trans light01Trans | | |
		 * | | sueloRot cubeTrans sphereTrans camera1 light01 | | |
		 * (*)sueloTrans | | | | | (*) suelo cube sphere
		 */
		N3GroupNode root = scene.getHierarchyRoot();
		scene.setAmbientalLight(new N3ColorRGBA(0f, 0f, 0f));

		grpScene = new N3GroupNode(scene, "grpScene");

		N3TransformationNode sueloRot = new N3TransformationNode(scene, "sueloRot");
		N3TransformationNode sueloTrans;
		sueloRot.rotate(-90.0f, new N3Vector3D(1.0f, 0, 0));
		sueloRot.update();
		N3GeometryData g = N3GeometryData.createPlane(0.4f, 0.4f, new N3ColorRGBA(1, 0, 0), false);
		N3Material sm = new N3Material(scene, "rojo");
		sm.setAmbientColor(new N3ColorRGBA(1, 0, 0));
		sm.setSpecularColor(new N3ColorRGBA(0, 0, 0), 0.1f);
		sm.setDiffuseColor(new N3ColorRGBA(1, 0, 0));
		sm.applyMaterial(true);
		for (int i = -5; i < 4; i++)
			for (int j = -5; j < 4; j++) {
				sueloTrans = new N3TransformationNode(scene, "sueloTrans" + i + "-" + j);
				sueloTrans.translate(new N3Vector3D(i * 0.4f, j * 0.4f, 0));
				sueloTrans.update();
				suelo = new N3VisualMesh(scene, g, "suelo" + i + "-" + j);
				suelo.setMaterial(sm);
				sueloTrans.addChild(suelo);
				sueloRot.addChild(sueloTrans);
			}

		cubeTrans = new N3TransformationNode(scene, "cubeTrans");
		cubeTrans.translate(new N3Vector3D(0, 0.2f, 1.0f));
		cubeTrans.update();

		// El otro cubo
		N3TransformationNode cubeTrans2 = new N3TransformationNode(scene, "cubeTrans2");
		cubeTrans2.translate(new N3Vector3D(-0.5f, 0.2f, -0.5f));
		cubeTrans2.update();

		cube = new N3VisualMesh(scene, N3GeometryData.createCube(0.4f, 0.4f, 0.4f, new N3ColorRGBA(1, 1, 1), true), "cube");
		cube.setMaterial(material);

		// El otro cubo
		cube2 = new N3VisualMesh(scene, N3GeometryData.createCube(0.4f, 0.4f, 0.4f, new N3ColorRGBA(1, 1, 1), true), "cube2");
		cube2.setMaterial(material);

		sphereTrans = new N3TransformationNode(scene, "sphereTrans");
		sphereTrans.translate(new N3Vector3D(0.5f, 0.5f, 0.0f));
		sphereTrans.update();

		sphere = new N3VisualMesh(scene, N3GeometryData.createSphere(20, 20, 0.15f, new N3ColorRGBA(0, 0, 1)), "sphere");
		sphere.setMaterial(new N3Material(scene, "Azul"));
		sphere.getMaterial().setAmbientColor(new N3ColorRGBA(0, 0, 1));
		sphere.getMaterial().setSpecularColor(new N3ColorRGBA(0, 0, 0), 0.1f);
		sphere.getMaterial().setDiffuseColor(new N3ColorRGBA(0, 0, 1));
		sphere.getMaterial().applyMaterial(true);

		camera1Trans = new N3TransformationNode(scene, "camera1Trans");
		camera1Trans.translate(new N3Vector3D(0.0f, 2.0f, 3.0f));
		camera1Trans.update();

		N3Camera camera1 = new N3Camera(new Rectangle(0, 0, c.getWidth(), c.getHeight()), 60.0f, c.getWidth() / c.getHeight(), 0.1f, 100.0f, scene, "camera1");

		light01Trans = new N3TransformationNode(scene, "light01Trans");
		light01Trans.translate(new N3Vector3D(0, 5.0f, 0.0f));
		light01Trans.update();

		light01 = new N3Light(N3LightData.createSpotLightData(new N3ColorRGBA(0, 0, 0), new N3ColorRGBA(1, 1, 1), new N3ColorRGBA(1, 1, 1), new N3Vector3D(0, -1.0f, 0), spotAngle), scene, "light01");

		root.addChild(grpScene);
		root.addChild(camera1Trans);
		root.addChild(light01Trans);
		grpScene.addChild(sueloRot);
		root.addChild(cubeTrans);
		grpScene.addChild(sphereTrans);
		sphereTrans.addChild(sphere);
		cubeTrans.addChild(cube);
		camera1Trans.addChild(camera1);
		light01Trans.addChild(light01);

		// El otro cubo
		cubeTrans2.addChild(cube2);
		grpScene.addChild(cubeTrans2);

		camera1.setTarget(cube, new N3Vector3D(0, 1.0f, 0));
		scene.setActiveCamera(camera1);
		scene.setActiveLightCount(render.getMaxLights());
		scene.addActiveLight(light01);

		// Elegimos que objetos colisionan
		sphere.setCollisionable(true);
		cube.setCollisionable(true);
		cube2.setCollisionable(true);
	}

	public void start() {
		t = new N3Timer(1, this);
		try {
			render = new N3SoftwareRender(c, true);
			render.setTexturing(true);
			render.setZBuffer(true);
			// render.setCullingFace(N3Render.N3_FRONT_CULL);
			render.setCullFacing(true);
			render.setLighting(true);

			loadMaterials();
			createScene();

			c.addKeyListener(this);		
			c.addMouseListener(this);

			display();
			/*
			 * N3xmlWriter xmlw = new N3xmlWriter(); xmlw.save(scene,new
			 * FileOutputStream("SceneTest.xml"));
			 */
			t.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void display() {
		scene.render(render);
	}

	int i = 0;
	long time;
	boolean start = false;

	public void idle() {
		if (!start) {
			time = System.currentTimeMillis();
			start = true;
		}
		sphereTrans.rotate(1, new N3Vector3D(0, 1, 0));
		sphereTrans.update();

		display();
		i++;
		long now = System.currentTimeMillis();
		if ((now - time) >= 5000) {
			System.out.println("" + i + " Frames in 5 seconds: " + ((int) (i / 5)) + " FPS");
			time = System.currentTimeMillis();
			i = 0;
		}
	}

	public void keyPressed(KeyEvent e) {
		 switch (e.getKeyCode()) {
            case KeyEvent.VK_L: 	light = !light;
                                	render.setLighting(light);
                                	break;
            case KeyEvent.VK_C: 	cullFacing = !cullFacing;
                                	if (cullFacing)
                                    	render.setCullFacing(true);
                                	else
                                    	render.setCullFacing(false);
                                	break;
	
			case KeyEvent.VK_UP: 	cubeTrans.translate(new N3Vector3D(0, 0, -.1f));
									break;
			case KeyEvent.VK_DOWN:	cubeTrans.translate(new N3Vector3D(0, 0, 0.1f)); 
									break;
			case KeyEvent.VK_RIGHT:	cubeTrans.rotate(-2f, new N3Vector3D(0, 1, 0));
									break;
			case KeyEvent.VK_LEFT:	cubeTrans.rotate(2f, new N3Vector3D(0, 1, 0));
									break;
			case KeyEvent.VK_PLUS:	light01.getLightData().setAngle(spotAngle++);
									break;
			case KeyEvent.VK_MINUS:	light01.getLightData().setAngle(spotAngle--);
									break;
        };

		cubeTrans.update();
        scene.updateBV();

		Vector v = scene.test(cube);
        v.remove(cube);
        if (v.size() > 0) {
            cubeTrans.undo();
        }
	}
	
	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		N3Ray ray = scene.getActiveCamera().getRay(new N3Point2D((float) e.getX(), (float) e.getY()), 1000.0f);
		Vector objects = scene.getHierarchyRoot().testRec(ray);
        if (objects.size() > 0) {
        	scene.Zorder(objects, scene.getActiveCamera().getPosition());
            N3VisualObject o = (N3VisualObject) objects.elementAt(0);
            if (o.testGeometry(ray, true))
            o.setBoundingVolumeVisible(!o.isBoundingVolumeVisible());
        }
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public static void main(String args[]) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SceneTest sceneTest = new SceneTest();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}
