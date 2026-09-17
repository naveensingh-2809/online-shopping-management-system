# TEST CASES SPECIFICATION
## STANDALONE ONLINE SHOPPING MANAGEMENT SYSTEM

---

| Test Case ID | Test Scenario | Description / Input | Expected Result | Pass / Fail Status |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Data Directory Auto-Creation | Launch app on clean directory without `data/` folder | `data/` directory and seed files (`users.txt`, `products.txt`) created automatically. | PASS |
| **TC-02** | Valid Customer Login | Email: `john@gmail.com`<br>Pass: `customer123` | Login successful; Customer Dashboard loaded with Welcome greeting. | PASS |
| **TC-03** | Invalid Customer Login | Email: `john@gmail.com`<br>Pass: `wrongpass` | Error dialog displayed: *"Invalid email or password"*. | PASS |
| **TC-04** | Valid Admin Login | Email: `admin@shopping.com`<br>Pass: `admin123` | Login successful; Admin Dashboard with stat cards loaded. | PASS |
| **TC-05** | Customer Registration | Name: *Bob Marley*, Email: *bob@gmail.com*, Pass: *bob12345* | User saved to `data/users.txt`; confirmation alert shown; user redirected to login. | PASS |
| **TC-06** | Product Keyword Search | Enter `"Headphones"` in search box | Table filters to show matching Wireless Headphones product. | PASS |
| **TC-07** | Category Filtering | Select category `"Electronics"` | Table displays only Electronics products. | PASS |
| **TC-08** | Add Product to Cart | Select product, Qty: `2`, click *Add to Cart* | Item added to `data/cart.txt`; cart tab total updated (`2 x price`). | PASS |
| **TC-09** | Insufficient Stock Check | Select product with stock `10`, set Qty: `15` | Error dialog: *"Requested quantity exceeds available stock"*. | PASS |
| **TC-10** | Cart Quantity Increment | Click `+` button on cart item | Quantity increases by 1; `data/cart.txt` updated; grand total updates. | PASS |
| **TC-11** | Cart Item Removal | Click `Remove Item` on cart row | Selected item removed from file & table; total re-calculated. | PASS |
| **TC-12** | Polymorphic Checkout (Card) | Select Card payment, enter valid card details | Payment authorized; order saved to `data/orders.txt`; cart cleared. | PASS |
| **TC-13** | Polymorphic Checkout (UPI) | Select UPI payment, enter `vpa@upi` | Instant authorization; order placed successfully. | PASS |
| **TC-14** | Receipt File Generation | Complete checkout flow | Background `OrderProcessingThread` writes `receipts/order_<id>.txt` file on disk. | PASS |
| **TC-15** | Admin Product Creation | Enter Name, Category, Price, Stock in Admin form | Product inserted into `data/products.txt` and displayed in table. | PASS |
| **TC-16** | Data Persistence Across Restart | Close app, re-launch app, login | All registered users, modified product stock, and placed orders remain intact. | PASS |
