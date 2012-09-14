Nu3A
====

Nu3A is a simple 3D library for Java.

The library was developed by the authors as their Computer Engineering 
final year project (2002/2003) at the University of Deusto.

**Authors:** Jorge García <bardok@gmail.com>, Unai Aguilera <gkalgan@gmail.com>

**License:** [GPL v3](http://www.gnu.org/licenses/gpl-3.0.html)

Features
--------

* Hierarchical engine
* Transformations
* Basic collision detection (bounding boxes)
* Object picking
* Pure software render with:
	* Light support (ambient and spot lights)
	* Textures
	* Basic materials
* Math classes (vector, matrix, ...)
* XML persistence of scenes (ad-hoc format)

Examples
--------

1. **Build**

		ant 

2. **Run an example**
	
* Triangle drawing test

		java -cp dist/nu3a-1.0.jar examples.BasicTest
   		
* A more complex scene

		java -cp dist/nu3a-1.0.jar examples.SceneTest
 
	* Press L to set light on/off
 	* Use + and - to increase or reduce the spot light angle
 	* Mouse click to select an object and show its bounding box
 	* Use cursors to move the nearest box