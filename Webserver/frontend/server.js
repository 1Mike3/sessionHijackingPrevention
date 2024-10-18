const { createServer } = require('node:http');
const { readFile } = require('node:fs');
const { join, extname } = require('node:path');

const hostname = '127.0.0.1';
const port = 3000;
const staticDir = 'static';

const server = createServer((req, res) => {
  // Determine the file path based on the request URL
  let filePath = join(__dirname, staticDir, req.url === '/' ? 'index.html' : req.url);
  
  // Determine the content type based on the file extension
  const ext = extname(filePath);
  let contentType = 'text/html'; // default content type
  switch (ext) {
    case '.js':
      contentType = 'application/javascript';
      break;
    case '.css':
      contentType = 'text/css';
      break;
    case '.html':
      contentType = 'text/html';
      break;
    case '.json':
      contentType = 'application/json';
      break;
    case '.png':
      contentType = 'image/png';
      break;
    case '.jpg':
    case '.jpeg':
      contentType = 'image/jpeg';
      break;
    case '.gif':
      contentType = 'image/gif';
      break;
    case '.svg':
      contentType = 'image/svg+xml';
      break;
    default:
      contentType = 'application/octet-stream';
  }

  // Read and serve the file
  readFile(filePath, (err, data) => {
    if (err) {
      res.statusCode = 404;
      res.setHeader('Content-Type', 'text/plain');
      res.end('404 Not Found');
    } else {
      res.statusCode = 200;
      res.setHeader('Content-Type', contentType);
      res.end(data);
    }
  });
});

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});
