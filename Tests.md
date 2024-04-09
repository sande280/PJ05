# Tests.md

This file documents the test cases that simulate user interactions with the Java Online Marketplace application. Perform these tests and verify your implementation works as expected.

## Test 1: User Registration (Seller)

Steps:
1. User launches the application.
2. User selects the "Register" option.
3. User selects the "Seller" role.
4. User enters a unique username and password.
5. User selects the "Register" button.

Expected result: The application successfully registers the user and displays a confirmation message.

Test Status: [Success]

## Test 2: User Registration (Customer)

Steps:
1. User launches the application.
2. User selects the "Register" option.
3. User selects the "Customer" role.
4. User enters a unique username and password.
5. User selects the "Register" button.

Expected result: The application successfully registers the user and displays a confirmation message.

Test Status: [Success]

## Test 3: User Login (Seller)

Steps:
1. User launches the application.
2. User selects the "Login" option.
3. User selects the "Seller" role.
4. User enters their correct username and password.
5. User selects the "Login" button.

Expected result: The application verifies the user's username and password, and loads their respective homepage automatically.

Test Status: [Success]

## Test 4: User Login (Customer)

Steps:
1. User launches the application.
2. User selects the "Login" option.
3. User selects the "Customer" role.
4. User enters their correct username and password.
5. User selects the "Login" button.

Expected result: The application verifies the user's username and password, and loads their respective homepage automatically.

Test Status: [Success]

## Test 5: Create a Store (Seller)

Steps:
1. Log in as a seller.
2. Navigate to the "Create Store" option.
3. Enter a unique store name and description.
4. Click the "Create Store" button.

Expected result: The application successfully creates a store for the seller and displays it on their homepage.

Test Status: [Success]

## Test 6: Add a Product to a Store (Seller)

Steps:
1. Log in as a seller.
2. Navigate to the "Manage Store" option.
3. Select the store to add the product.
4. Enter the product name, price, and description.
5. Click the "Add Product" button.

Expected result: The application successfully adds the product to the selected store, and the product is visible in the store's product list.

Test Status: [Success]

## Test 7: Remove a Product from the Store (Seller)

Steps:
1. Log in as a seller.
2. Navigate to the "Manage Store" option.
3. Select the store containing the product to be removed.
4. Click the "Remove Product" button next to the product.

Expected result: The application successfully removes the product from the selected store, and the product is no longer visible in the store's product list.

Test Status: [Success]

## Test 8: Update Product Information (Seller)

Steps:
1. Log in as a seller.
2. Navigate to the "Manage Store" option.
3. Select the store containing the product to be updated.
4. Click the "Edit Product" button next to the product.
5. Update the product name, price, and/or description.
6. Click the "Save Changes" button.

Expected result: The application successfully updates the product information, and the updated information is visible in the store's product list
Test Status: [Success]

## Test 9: Add a Product to the Shopping Cart (Customer)

Steps:
1. Log in as a customer.
2. Browse or search for a product.
3. Select the desired product.
4. Click the "Add to Cart" button.

Expected result: The application successfully adds the product to the shopping cart, and the product is visible in the cart.

Test Status: [Success]

## Test 10: Checkout (Customer)

Steps:
1. Log in as a customer.
2. Add one or more products to the shopping cart.
3. Navigate to the "Shopping Cart" option.
4. Review the list of items in the cart and click the "Checkout" button.

Expected result: The application processes the customer's order and displays a confirmation message.

Test Status: [Success]

## Test 11: Log Out

Steps:
1. Log in as a customer or seller.
2. Perform any desired actions within the application.
3. Click the "Log Out" button.

Expected result: The application successfully logs the user out and returns to the login/registration screen.

Test Status: [Success]

## Test 12: Seller Modifies Their Store

Steps:
1. User logs in as a seller.
2. User selects the "Modify Store" option.
3. User modifies store details and submits the form.

Expected result: Application updates the store information as per the modifications made by the seller.

Test Status: [Success]

## Test 13: Customer Views the Marketplace

Steps:
1. User logs in as a customer.
2. User selects the "View Marketplace" option.

Expected result: Application displays the list of stores and their products in the marketplace.

Test Status: [Success]
## Test 14: Customer Searches for a Product in the Marketplace

Steps:
1. User logs in as a customer.
2. User selects the "Search Marketplace" option.
3. User enters search keywords and submits the form.

Expected result: Application displays the search results based on the entered keywords.

Test Status: [Success]

## Test 15: Customer Adds a Product to Their Shopping Cart

Steps:
1. User logs in as a customer.
2. User views the marketplace or searches for a product.
3. User selects a product and adds it to their shopping cart.

Expected result: Application adds the selected product to the customer's shopping cart.

Test Status: [Success]

## Test 16: Customer Views Their Shopping Cart

Steps:
1. User logs in as a customer.
2. User selects the "View Shopping Cart" option.

Expected result: Application displays the list of items in the customer's shopping cart.

Test Status: [Success]

