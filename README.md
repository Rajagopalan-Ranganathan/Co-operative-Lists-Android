# Coop - Collaborative Lists

## Technology

Frontend - Android
Backend - Firebase

## Developers
Ivan Shabunin
Max Reuter
Rajagopalan Ranganathan
Sunil Kumar Mohanty



## Functionality

### User Login

### Shopping list

* Add new shopping list

  New list can be added by typing the name in the filter (search) text box. If the list is not present, then the same shall get created. To create and save the list, the user has to click on the shopping list from the list. The user is automatically added as an owner and a member.

* Click of the list
  On a single click of a list, the user is taken to a new screen which displays
    * Items in the shopping list
    * Items not in the shopping list


* Deleting the list

  A list can be deleted by the action - "long click" on the list. User will be presented with a dialog box to confirm the action. The list will be deleted only on confirmation.


* Filter - List can be filtered by typing the filter text on the search box on the top of the screen. It only filters based on the shopping list name.

### Shopping List Details

* Screen
  The screen is broken down into two tabs.
    * First tab (List) shows the list of items already in the shopping list
    * Second tab (Add) shows the list of items which are not yet added to the list


* Add item to the list

  One can add item to the list by going to the "Add" tab and clicking on the item. On adding the item, the quantity is set to 1.

* Removing an item from the list

  One can remove an item from the list by long cliking on the item in the "List" tab

* Adding a new item not present in the list

  One can add a new item which is not present in the list by searching for the item in the search box in the "Add" tab. Once user clicks on the item, the item gets created as a custom item (only available to its creator) and then gets added to the shopping list. The system provides some items which are available by default for everyone.

* Check Item Details

  User can click on an individual item in the "List" tab and this will take them to the Item Details screen where the user can assign the item to himself and can also change the quantity.

* Check members

  User can check the members of the shopping list by clicking on the members option from the menu.


### Item details screen

  * Change quantity
    The screen shows the current quantity for the item. User can modify the quantity by just typing the quantity in the text box.

  * Assign the item to self
    User can assign to self by clicking on the "Assign to me" button


### Members details screen

  * The screen shows the list of members belonging to the list
  * User can be added by typing the email id of the user and pressing the "ADD" button
  * Member can be removed by long click on the member
  * Owner cannot be removed
