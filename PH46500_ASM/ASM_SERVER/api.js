const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const carModel = require('./carModel');
const COMMON = require('./COMMON');
const cartModel = require('./cartModel');
const billModel = require('./billModel');
const billDetailModel = require('./billDetailModel');
router.get('/', (req, res) => {
    res.send('vao api mobile');
});

router.get('/list', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let cars = await carModel.find();
    console.log(cars);
    res.send(cars);
});

router.delete('/delete/:id', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let id = req.params.id;
    console.log(id);
    await carModel.deleteOne({ _id: id });
    res.redirect('/api/list');
});

router.get('/car/:id', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let id = req.params.id;
    let car = await carModel.findById(id);
    if (car) {
        res.send(car);
    } else {
        res.status(404).send({ error: 'Car not found' });
    }
});

router.post('/add', async (req, res) => {
    try {
        let car = req.body;
        console.log(car);
        delete car._id; // Ensure _id is not provided
        let newCar = await carModel.create(car);
        console.log(newCar);
        // Return the newly created car
        res.send(newCar);
    } catch (err) {
        console.error(err);
        res.status(500).send('Error adding car');
    }
});


router.put('/update/:id', async (req, res) => {
    try {
        await mongoose.connect(COMMON.uri);
        let id = req.params.id;
        let carUpdates = req.body;
        console.log(carUpdates);
        
        let updatedCar = await carModel.findByIdAndUpdate(id, carUpdates, { new: true });
        
        if (updatedCar) {
            res.send(updatedCar);
        } else {
            res.status(404).send({ error: 'Car not found' });
        }
    } catch (err) {
        console.error(err);
        res.status(500).send('Error updating car');
    }
});

router.get('/cart/:userId', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let cart = await cartModel.findOne({ userId: 1 });
    if (cart) {
        res.send(cart);
    } else {
        res.status(404).send({ error: 'Cart not found' });
    }
});

router.post('/addcart', async (req, res) => {
    try {
        let newCart = await cartModel.create(req.body);
        res.send(newCart);
    } catch (err) {
        console.error(err);
        res.status(500).send('Error creating cart');
    }
});
router.put('/updatecart/:cartId', async (req, res) => {
    const cartId = req.params.cartId;
    const { items, totalPrice } = req.body;

    if (!items || !Array.isArray(items) || items.length === 0 || totalPrice === undefined) {
        return res.status(400).send('Invalid request data. Ensure items and totalPrice are provided.');
    }

    try {
        // Find and update the cart
        const updatedCart = await cartModel.findByIdAndUpdate(
            cartId,
            { items, totalPrice },
            { new: true, runValidators: true }
        );

        if (!updatedCart) {
            return res.status(404).send('Cart not found');
        }

        res.json(updatedCart);
    } catch (err) {
        console.error('Error updating cart:', err.message);
        res.status(500).send('Error updating cart: ' + err.message);
    }
});

router.put('/updatecart/:userId', async (req, res) => {
    try {
        await mongoose.connect(COMMON.uri);
        let userId = 1;
        let cartUpdates = req.body;
        let updatedCart = await cartModel.findOneAndUpdate(
            { userId: userId },
            { $set: cartUpdates },
            { new: true }
        );
        res.send(updatedCart);
    } catch (err) {
        console.error(err);
        res.status(500).send('Error updating cart');
    }
});

router.delete('/deletecart/:userId', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let userId = 1;
    await cartModel.deleteOne({ userId: userId });
    res.send({ message: 'Cart deleted' });
});
router.delete('/deleteSp/:itemId', async (req, res) => {
    try {
        // Connect to MongoDB (if not already connected)
        await mongoose.connect(COMMON.uri); // Replace with your MongoDB URI

        let cartId= '66b0a9f0f767b71d56510a57';
        const { itemId } = res.params; // Expecting itemId to be sent in the request body

        if (!itemId) {
            return res.status(400).send('Item ID is required');
        }

        // Find the cart by cartId
        const cart = await cartModel.findById(cartId);

        if (!cart) {
            return res.status(404).send('Cart not found');
        }

        // Check if the item exists in the cart
        const itemExists = cart.items.some(item => item._id.toString() === itemId);

        if (!itemExists) {
            return res.status(404).send('Item not found in cart');
        }

        // Remove the item from the cart's items array
        cart.items = cart.items.filter(item => item._id.toString() !== itemId);
        await cart.save();

        res.send({ message: 'Item removed from cart', cart });
    } catch (err) {
        console.error('Error removing item from cart:', err.message);
        res.status(500).send('Error removing item from cart: ' + err.message);
    }
});

// Routes cho Bill
router.get('/bill/:userId', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let bills = await billModel.find({ userId: 1 });
    res.send(bills);
});

router.post('/addbill', async (req, res) => {
    try {
        let newBill = await billModel.create(req.body);
        res.send(newBill);
    } catch (err) {
        console.error(err);
        res.status(500).send('Error creating bill');
    }
});

router.put('/updatebill/:id', async (req, res) => {
    try {
        await mongoose.connect(COMMON.uri);
        let id = req.params.id;
        let billUpdates = req.body;
        let updatedBill = await billModel.findByIdAndUpdate(id, billUpdates, { new: true });
        res.send(updatedBill);
    } catch (err) {
        console.error(err);
        res.status(500).send('Error updating bill');
    }
});

router.delete('/deletebill/:id', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let id = req.params.id;
    await billModel.deleteOne({ _id: id });
    res.send({ message: 'Bill deleted' });
});

// Routes cho BillDetail
router.get('/billdetail/:id', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let billDetail = await billDetailModel.findById(req.params.id);
    if (billDetail) {
        res.send(billDetail);
    } else {
        res.status(404).send({ error: 'Bill detail not found' });
    }
});

router.post('/addbilldetail', async (req, res) => {
    try {
        let newBillDetail = await billDetailModel.create(req.body);
        res.send(newBillDetail);
    } catch (err) {
        console.error(err);
        res.status(500).send('Error creating bill detail');
    }
});

router.put('/updatebilldetail/:id', async (req, res) => {
    try {
        await mongoose.connect(COMMON.uri);
        let id = req.params.id;
        let billDetailUpdates = req.body;
        let updatedBillDetail = await billDetailModel.findByIdAndUpdate(id, billDetailUpdates, { new: true });
        res.send(updatedBillDetail);
    } catch (err) {
        console.error(err);
        res.status(500).send('Error updating bill detail');
    }
});

router.delete('/dletebilldetail/:id', async (req, res) => {
    await mongoose.connect(COMMON.uri);
    let id = req.params.id;
    await billDetailModel.deleteOne({ _id: id });
    res.send({ message: 'Bill detail deleted' });
});
// Route để lấy danh sách BillDetail từ idBill
router.get('/billdetails/:idBill', async (req, res) => {
    try {
        // Kết nối đến MongoDB
        await mongoose.connect(COMMON.uri);
        
        // Lấy idBill từ params
        let idBill = req.params.idBill;
        
        // Tìm tất cả các BillDetail có idBill tương ứng
        let billDetails = await billDetailModel.find({ idBill: idBill });
        
        // Kiểm tra nếu tìm thấy BillDetail
        if (billDetails.length > 0) {
            res.send(billDetails);
        } else {
            res.status(404).send({ error: 'No bill details found for this bill ID' });
        }
    } catch (err) {
        console.error('Error fetching bill details:', err.message);
        res.status(500).send('Error fetching bill details: ' + err.message);
    }
});
router.put('/update-bill-status/:idBill', async (req, res) => {
    try {
        // Kết nối đến MongoDB
        await mongoose.connect(COMMON.uri);
        
        // Lấy idBill từ params và trạng thái mới từ body
        let idBill = req.params.idBill;
        let status  = 'Paid';
     
        
        // Cập nhật trạng thái của hóa đơn
        let updatedBill = await billModel.findByIdAndUpdate(
            idBill,
            { status: status },
            { new: true, runValidators: true }
        );
        
        // Kiểm tra nếu cập nhật thành công
        if (updatedBill) {
            res.send(updatedBill);
        } else {
            res.status(404).send({ error: 'Bill not found' });
        }
    } catch (err) {
        console.error('Error updating bill status:', err.message);
        res.status(500).send('Error updating bill status: ' + err.message);
    }
});


// Endpoint để lấy danh sách sản phẩm từ billDetail
router.get('/products-from-billdetail/:billId', async (req, res) => {
    try {
        await mongoose.connect(COMMON.uri);
        const { billId } = req.params;

        // Tìm tất cả các BillDetail với billId
        const billDetails = await billDetailModel.find({ billId: billId }).populate('productId');

        if (billDetails.length > 0) {
            // Trả về danh sách các sản phẩm có trong billDetail
            const products = billDetails.map(detail => detail.productId);
            res.send(products);
        } else {
            res.status(404).send({ error: 'No products found for this bill detail' });
        }
    } catch (err) {
        console.error('Error fetching products from bill details:', err.message);
        res.status(500).send('Error fetching products from bill details: ' + err.message);
    }
});

module.exports = router;


module.exports = router;
