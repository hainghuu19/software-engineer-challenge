# Ad Performance Aggregator (FV-SEC001)

CLI tool that **streams** a large advertising CSV (~1GB), aggregates metrics per `campaign_id`, and writes:

- `top10_ctr.csv` — top 10 campaigns by **CTR** (descending)
- `top10_cpa.csv` — top 10 campaigns by **lowest CPA** (campaigns with zero total conversions excluded)

Implemented in **plain Java 21** with **Maven**. No runtime dependencies beyond the JRE.

---

## Requirements

- **Docker** (Docker Engine or Docker Desktop) — to run the application as documented below
- **JDK 21+** and **Maven 3.9+** — only if you run unit tests or build outside Docker (`mvn test`, `mvn package`)
- Input CSV columns: `campaign_id`, `date`, `impressions`, `clicks`, `spend`, `conversions`

Place (or unzip) `ad_data.csv` on the host (for example under `./data/ad_data.csv`) and mount that folder into the container — see [How to run](#how-to-run).

---

## CLI

Arguments are passed to the Java process (after the image name in `docker run`).

| Argument | JVM default (no args) | Docker image default (`CMD`) |
|----------|-------------------------|------------------------------|
| `--input` | `data/ad_data.csv` | `/data/ad_data.csv` |
| `--output` | `output` | `/out` |

The output directory is created if it does not exist. If the input file is missing or not readable, the process exits non-zero and prints a message on stderr.

**Important:** Inside the container, `--input` and `--output` must be paths **visible inside the container** (usually under your `-v` mounts).

**Override flags (Docker)**

```bash
docker run --rm \
  -v "$(pwd)/data:/data:ro" \
  -v "$(pwd)/output:/out" \
  ad-aggregator \
  --input /data/ad_data.csv \
  --output /out
```

---

## How to run

From the **repository root** (directory that contains `Dockerfile` and `pom.xml`).

### Build image

```bash
docker build -t ad-aggregator .
```

### Run (default: read `/data/ad_data.csv`, write to `/out`)

Mount host `./data` and `./output` so those container paths exist. Example:

**Linux / macOS**

```bash
docker run --rm \
  -v "$(pwd)/data:/data:ro" \
  -v "$(pwd)/output:/out" \
  ad-aggregator
```

**Windows PowerShell**

```powershell
docker run --rm `
  -v "${PWD}/data:/data:ro" `
  -v "${PWD}/output:/out" `
  ad-aggregator
```

## Tests

```bash
mvn test
```

Coverage includes CSV parsing, streaming aggregation, CLI defaults, CTR/CPA ranking, and CSV report writing (temporary files).

---

## Libraries & build

| Scope | Notes |
|-------|--------|
| Runtime | JDK standard library only |
| Test | [JUnit 5](https://junit.org/junit5/) |

Maven plugins: **`maven-jar-plugin`** (executable JAR with `Main-Class: org.example.Main`), **`exec-maven-plugin`** (`exec:java`), **`maven-surefire-plugin`**.

Artifact name: `target/software-engineer-challenge-1.0-SNAPSHOT.jar`.

---

## Design notes

- **Memory:** one CSV line at a time via `BufferedReader`; aggregates in a `HashMap<String, CampaignStats>`. Memory grows with **distinct `campaign_id` values**, not raw file size.
- **Malformed rows:** skipped; the program prints how many data rows were skipped.
- **Header:** if the first line starts with `campaign_id` (case-insensitive), it is treated as a header. Otherwise the first line is data.
- **CTR:** `total_clicks / total_impressions`; if `total_impressions == 0`, CTR is `0.0`.
- **CPA:** `total_spend / total_conversions`; if `total_conversions == 0`, CPA is unset (empty field in CSV).
- **Output numbers:** CTR, spend, and CPA use six decimal places (`Locale.ROOT`).

---

## Processing time

Replace the placeholders with your own measurements on the ~1GB file before submission.

**Example:** On a typical developer machine, **tens of millions of rows** can finish in about **20–30 seconds** wall-clock (depends on CPU and disk).

**Wall time**

- Linux / macOS: `time docker run --rm -v "$(pwd)/data:/data:ro" -v "$(pwd)/output:/out" ad-aggregator`
- Windows PowerShell: `Measure-Command { docker run --rm -v "${PWD}/data:/data:ro" -v "${PWD}/output:/out" ad-aggregator }`

---

## AI assistance

This project used an AI coding assistant for parts of the implementation. See **`PROMPTS.md`** for raw prompts and a short workflow description.

---

## Challenge / outputs

Submission target: **Flinters** software engineer challenge. Expected artifacts include `output/top10_ctr.csv` and `output/top10_cpa.csv` (paths may differ if you override `--output`).
