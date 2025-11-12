# 📊 JaCoCo Integration Guide

## ✅ What Was Added

The **JaCoCo Maven Plugin** has been integrated into your `pom.xml` to measure code coverage.

## 🚀 How to Use JaCoCo

### 1. Run Tests with Coverage

```bash
mvn clean test
```

This will:
- Run all your tests
- Collect coverage data
- Generate coverage reports

### 2. View Coverage Reports

After running tests, reports are generated in:
```
target/site/jacoco/index.html
```

**Open this file in your browser** to see:
- ✅ Line coverage percentage
- ✅ Branch coverage percentage
- ✅ Method coverage
- ✅ Class coverage
- 🎨 Color-coded source code (green = covered, red = not covered)

### 3. Generate Report Without Running Tests

If tests already ran:
```bash
mvn jacoco:report
```

### 4. Check Coverage Thresholds

The plugin is configured to **enforce minimum coverage**:
- **60% line coverage** minimum
- **50% branch coverage** minimum

To check if your code meets these thresholds:
```bash
mvn jacoco:check
```

If coverage is below thresholds, the build will **fail** with a detailed message.

---

## 📈 Understanding the Reports

### Coverage Metrics

| Metric | Description |
|--------|-------------|
| **Line Coverage** | % of executable lines that were executed |
| **Branch Coverage** | % of decision branches (if/else) that were taken |
| **Method Coverage** | % of methods that were called |
| **Class Coverage** | % of classes that were instantiated |

### Report Colors

- 🟢 **Green**: Fully covered (100%)
- 🟡 **Yellow**: Partially covered (1-99%)
- 🔴 **Red**: Not covered (0%)

---

## 🎯 Example Workflow

### Step 1: Run Tests with Coverage
```bash
cd C:\Users\chong\Documents\GitHub\CS203\tariff-backend
mvn clean test
```

### Step 2: Open Coverage Report
1. Navigate to: `target/site/jacoco/index.html`
2. Open in browser (Chrome, Edge, Firefox, etc.)
3. Click on packages → classes → methods to drill down

### Step 3: Improve Coverage
- Look for **red lines** (uncovered code)
- Write tests to cover those lines
- Run `mvn test` again to see improvement

---

## 🔧 Customizing Coverage Thresholds

Edit `pom.xml` to change minimum coverage requirements:

```xml
<configuration>
    <rules>
        <rule>
            <element>PACKAGE</element>
            <limits>
                <limit>
                    <counter>LINE</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.80</minimum> <!-- Change to 80% -->
                </limit>
                <limit>
                    <counter>BRANCH</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.70</minimum> <!-- Change to 70% -->
                </limit>
            </limits>
        </rule>
    </rules>
</configuration>
```

---

## 📦 What Files Are Generated?

After running tests, you'll see:

```
target/
├── jacoco.exec              # Binary coverage data
└── site/
    └── jacoco/
        ├── index.html       # 👈 Main coverage report (OPEN THIS)
        ├── jacoco.xml       # XML format (for CI/CD tools)
        ├── jacoco.csv       # CSV format (for spreadsheets)
        └── [package folders with detailed reports]
```

---

## 🎨 Visual Example

When you open `index.html`, you'll see something like:

```
┌────────────────────────────────────────────────────────┐
│  Element          │ Missed  │ Cov.   │ Total   │ %    │
├────────────────────────────────────────────────────────┤
│  CS203G3.model    │   45    │  155   │  200    │ 78%  │
│  CS203G3.service  │   30    │  120   │  150    │ 80%  │
│  CS203G3.controller│  20    │   80   │  100    │ 80%  │
└────────────────────────────────────────────────────────┘
```

Click on any package to see **which specific lines** are covered!

---

## 🚦 Integration with CI/CD

### GitHub Actions Example

```yaml
name: Test with Coverage

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Run tests with coverage
        run: mvn clean test
      
      - name: Upload coverage report
        uses: actions/upload-artifact@v3
        with:
          name: jacoco-report
          path: target/site/jacoco/
```

---

## 🎓 Best Practices

1. **Aim for 80%+ coverage** for production code
2. **Focus on critical paths** (business logic, security)
3. **Don't chase 100%** - some code is hard to test (getters/setters)
4. **Review coverage reports** before merging PRs
5. **Exclude generated code** from coverage if needed

---

## ❓ Troubleshooting

### "No tests were executed"
```bash
# Make sure tests exist and run successfully first
mvn test
```

### "Report not generated"
```bash
# Clean and rebuild
mvn clean test
```

### "Coverage is 0%"
- Check that tests are actually running
- Verify JaCoCo agent is attached (should see in test output)

---

## 📚 Additional Commands

| Command | Description |
|---------|-------------|
| `mvn jacoco:help` | Show JaCoCo plugin help |
| `mvn jacoco:dump` | Dump coverage data |
| `mvn verify` | Run tests + coverage check in one command |

---

## 🎉 You're All Set!

Run `mvn clean test` and check your coverage report at:
**`target/site/jacoco/index.html`**

Happy testing! 🚀
