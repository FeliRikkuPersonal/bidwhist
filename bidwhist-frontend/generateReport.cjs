// generateReport.js

const fs = require('fs');
const path = require('path');

const inputFile = path.join(__dirname, 'vitest-report.json');
const outputFile = path.join(__dirname, 'vitest-report.html');

const json = JSON.parse(fs.readFileSync(inputFile, 'utf8'));

const html = `
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Vitest Report</title>
<style>
  body {
    font-family: Arial, sans-serif;
    font-size: 11px;
    line-height: 1;
    padding: 1.2rem;
    background: #f9f9f9;
    color: #333;
  }

  h1 {
    font-size: 16px;
    margin-bottom: 0.5rem;
    color: #222;
  }

  h2 {
    font-size: 13px;
    margin-top: 1.5rem;
    margin-bottom: 0.5rem;
    border-bottom: 1px solid #ddd;
    padding-bottom: 0.25rem;
  }

  .test-file {
    margin-bottom: 1rem;
  }

  .test {
    border-left: 4px solid #ccc;
    padding: 0.5rem;
    margin: 0.5rem 0;
    background: #fff;
    font-size: 12px;
  }

  .passed {
    border-color: green;
  }

  .failed {
    border-color: red;
    background: #fff1f1;
  }

  .test-title {
    font-weight: bold;
    margin-bottom: 0.25rem;
  }

  .error {
    color: #c00;
    white-space: pre-wrap;
    font-size: 12px;
    margin-top: 0.25rem;
  }
</style>

</head>
<body>
  <h1>Vitest Test Report</h1>
  <p><strong>Total:</strong> ${json.numTotalTests} | ✅ Passed: ${json.numPassedTests} | ❌ Failed: ${json.numFailedTests}</p>
  ${json.testResults.map(file => `
    <div class="test-file">
      <h2>${file.name}</h2>
      ${file.assertionResults.map(test => `
        <div class="test ${test.status}">
          <div class="test-title">${test.fullName}</div>
          ${test.status === 'failed' ? `<div class="error">${test.failureMessages.join('\n\n')}</div>` : ''}
        </div>
      `).join('')}
    </div>
  `).join('')}
</body>
</html>
`;

fs.writeFileSync(outputFile, html, 'utf8');
console.log(`✅ HTML report generated: ${outputFile}`);
