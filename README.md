# Overview
WaresMyStuff is a minimal warehouse inventory management program. It tracks names of parts, quantity of parts, part's locations, and when a part or location was last inventoried. WaresMyStuff uses Apache Tomcat for the User Interface and MySql as a database. Java is the programming language.

Locations use a tree structure so a location can contain other locations (as well as parts).
The user interface contains three tabs, one tab for the part and two tabs for locations. A part tab and location tab combination is used to add and remove parts from the warehouse. The part tab is used to specify the part, the location tab is used to specify the location, and the combination of the two tabs allows the user to locate the specified part, find the parts at a location, or change the number of parts at a location.

A location tab and location tab combination is used to move locations from one location to another location. For example, if a file cabinet in Bob's office is a location, and the file cabinet is moved to the loading dock, the file cabinet would be disassociated from the "Bob's Office" branch and made a branch of the "Loading Dock". All references to the contents of the file cabinet are automatically updated as being part of the "Loading Dock" branch.
# XML Configuration File

# Apache Tomcat
# MySql
# User Interface
## Parts
### Selecting the Parts tab
### Adding a Part
### Changing the Name of a Part
### Finding a Part
### Adding and Removing Parts from Inventory
### Updating the Inventory History of a Part
## Locations
### Selecting the Locations tab
### Adding a Location
### Changing the Name of a Location
### Finding a Location
### Adding and Removing Parts from Inventory
### Updating the Inventory History of a Location
### Moving to the Parent of a Location
### Moving to the Child of a Location
### Locations without Parents
