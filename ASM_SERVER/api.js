const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const carModel = require('./carModel');
const COMMON = require('./COMMON');

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

module.exports = router;
