const express = require('express');
const app = express();
const port = 3000;
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const COMMON = require('./COMMON');
const carModel = require('./carModel');
const apiMobile = require('./api');

const uri = COMMON.uri;

// Middleware to parse JSON and URL-encoded data
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`);
});

app.use('/api', apiMobile);

app.get('/', async (req, res) => {
    await mongoose.connect(uri);

    let cars = await carModel.find();

    console.log(cars);

    res.send(cars);
});

app.post('/add_xe', async (req, res) => {
    await mongoose.connect(uri);

    let car = req.body;

    console.log(car);

    let kq = await carModel.create(car);
    console.log(kq);

    let cars = await carModel.find();

    res.send(cars);
});

app.get('/xoa/:id', async (req, res) => {
    await mongoose.connect(uri);

    let id = req.params.id;
    console.log(id);

    await carModel.deleteOne({ _id: id });

    res.redirect('../');
});

app.put('/update/:id', async (req, res) => {
    try {
        await mongoose.connect(uri);
        console.log('Kết nối DB thành công');

        let id = req.params.id;
        let carUpdates = req.body;

        let updatedCar = await carModel.findByIdAndUpdate(id, carUpdates, { new: true });

        if (updatedCar) {
            res.send(updatedCar);
        } else {
            res.status(404).send({ error: 'Car not found' });
        }
    } catch (err) {
        console.error(err);
        res.status(500).send('Error updating car');
    } finally {
        mongoose.connection.close();
    }
});
