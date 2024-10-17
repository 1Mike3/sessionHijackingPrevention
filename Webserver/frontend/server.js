// # Modules
const { createServer } = require('node:http');
const { readFile } = require('node:fs');
const { join } = require('node:path');
// # Netw Params
const hostname = '127.0.0.1';
const port = 3000;
// # Vars
const staticDir = 'static'; 

const server = createServer((req, res) => {
  const filePath = join(__dirname, staticDir, 'index.html');
  // Read the file and send it as the response
  readFile(filePath, (err, data) => {
    if (err) {
      res.statusCode = 500;
      res.setHeader('Content-Type', 'text/plain');
      res.end('Error loading the page');
    } else {
      res.statusCode = 200;
      res.setHeader('Content-Type', 'text/html');
      res.end(data);
    }
  });
});
server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});
