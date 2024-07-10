const http = require('node:http');
const hostname = '127.0.0.1'; //localhost
const port = 3003;
const server = http.createServer((req, res) => {
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/html; charset=utf-8');
    res.write('<h1>Lab2 - NodeJS</h1>');
    res.write('<br>');
    res.write('<p>Nội dung đoạn văn bản</p>');

    res.write(`<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <h1>Nội dung HTML5</h1>
    <img src="banner.jpg" width="100%">
    <p>Văn bản HTML 5</p>
</body>
</html>`);


    res.end();
});


server.listen(port, hostname, () => {
    console.log(`Server running at http://${hostname}:${port}/`);
});