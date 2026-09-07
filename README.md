# ChikitsaLipi: Android Application for OCR-Based Digitization, Structured Extraction, and Multilingual Translation of Printed and Handwritten Health Records

## Summary

**ChikitsaLipi** is an open-source, research-oriented Android application designed for optical character recognition (OCR), structured information extraction, multilingual translation, and local digital preservation of physical health records. Targeted primarily at resource-constrained, rural, low-connectivity, and multilingual environments, ChikitsaLipi enables users to convert physical medical artifacts—including printed prescriptions, laboratory reports, clinical progress notes, diagnostic reports, referral documents, and handwritten medical records—into structured, machine-readable, and locally saved digital records.

The central design philosophy prioritizes human-in-the-loop verification, transparent uncertainty presentation, WCAG 2.2 AA accessibility, minimal cognitive load, and strict data privacy through local-first on-device processing.

---

## Technical Details

### Key Capabilities

- **On-Device Image Acquisition & Quality Assessment**: Real-time camera capture using CameraX with automated edge/document boundary detection and real-time visual assessment of ambient illumination, motion blur, and framing.
- **Image Preprocessing Subsystem**: Automated contrast normalization, grayscale conversion, perspective correction, rotation alignment, and adaptive binarization to enhance OCR accuracy across heterogenous physical document types.
- **Hybrid OCR & Text Recognition Engine**: On-device text recognition optimized for printed Latin script, Devanagari script, and Bengali script, supplemented by specialized handwriting handling models.
- **Deterministic Medical Field Extraction**: Regex-based pattern matching, clinical dictionary lookups, and context-window extraction for vital signs, laboratory parameters, numeric values, and clinical units.
- **Multilingual Translation Subsystem**: Structured field-level translation into Bengali (BN) and Hindi (HI) while strictly maintaining numeric, unit, and structural parity across target languages.
- **Human-in-the-Loop Verification Interface**: Dual-pane visual interface correlating source image regions (bounding boxes) with extracted text, uncertainty indicators, and specialized keypad editors for manual user correction.
- **Local Encrypted Storage & Traceability**: Room database persistence backed by Android Keystore, preserving original OCR outputs, user modifications, and generated translations with complete audit traceability.

---

## System Architecture

The application is built following Clean Architecture and MVVM (Model-View-ViewModel) design principles, enforcing strict separation of concerns across nine functional layers:

```
+-------------------------------------------------------------------+
|                        Presentation Layer                         |
|    (Jetpack Compose UI, Dynamic Font Scaling, Accessibility)     |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                         Camera Layer                              |
|          (CameraX, Real-Time Quality Assessment, Framing)         |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                    Image Processing Layer                         |
|     (Perspective Correction, Binarization, Contrast, Crop)        |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                          OCR Layer                                |
|  (Google ML Kit Text Recognition: Latin, Devanagari, Bengali)    |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                     Text Processing Layer                         |
|      (Language Identification, Cleaning, Line Segmentation)       |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                   Medical Extraction Layer                        |
|   (Regex, Clinical Dictionaries, Value & Unit Pattern Matching)   |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                 Verification & Validation Layer                   |
|  (Confidence Scoring, Consistency Rules, Uncertainty Tagging)     |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                    Translation Subsystem                          |
|    (English -> Bengali / Hindi, Value & Unit Parity Assurance)    |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                   Storage & Export Layer                          |
|     (Room Database, Android Keystore, Local JSON/PDF Export)      |
+-------------------------------------------------------------------+
```

### Detailed Flowchart

```mermaid
flowchart TD
    A[Start] --> B[Home Screen]
    B --> C{User Action}
    C -->|Digitize New Record| D[Camera Interface]
    C -->|View Saved Records| Z[Saved Records Directory]

    D --> E{Camera Permission Granted?}
    E -- No --> F[Request Camera Permission]
    F --> E
    E -- Yes --> G[Live Camera Preview]

    G --> H[Image Quality Assessment]
    H --> I{Quality Check Passed?}
    I -- Poor Light / Blur / Framing Issue --> J[Display Quality Warning Banner]
    J --> K{User Action}
    K -- Retake --> G
    K -- Proceed Anyway --> L[Capture Document Image]
    I -- Quality Acceptable --> L

    L --> M[Image Preprocessing Engine]
    M --> N[Contrast Enhancement & Perspective Correction]
    N --> O[On-Device OCR Engine]

    O --> P{OCR Execution Successful?}
    P -- Failure / No Text --> Q[Display OCR Failure Error]
    Q --> K
    P -- Success --> R[Raw Text & Bounding Boxes Generated]

    R --> S[Language Identification]
    S --> T[Medical Entity Extraction Engine]
    T --> U[Value & Unit Regex Pattern Detection]
    U --> V[Validation & Confidence Assessment]

    V --> W{Confidence / Completeness Assessment}
    W -- High Confidence & Complete --> X[Mark Status: High Confidence]
    W -- Missing Unit / Ambiguous Value --> Y[Mark Status: Needs Review Tagged Uncertain]

    X --> AA[Multilingual Translation Subsystem]
    Y --> AA

    AA --> AB[Translate Field Labels to Bengali & Hindi]
    AB --> AC[Results & Verification Screen]

    AC --> AD{User Verification Decision}
    AD -- User Identifies Uncertainty / Error --> AE[Manual Correction Interface Sheet]
    AE --> AF[Specialized Medical Keypad Input]
    AF --> AG[Confirm and Update Field]
    AG --> AH[Update Status: User Verified]
    AH --> AC

    AD -- User Approves Record --> AI[Save Record to Local Encrypted Storage]

    AI --> AJ[Record Detail View]
    AJ --> AK{User Management Options}
    AK -- Export Record --> AL[Export to Structured JSON / Text / PDF]
    AK -- Delete Record --> AM[Secure Local Deletion Confirmation]
    AM --> AN[Purge File & Database Entry]
    AN --> Z
    AL --> Z

    Z --> AO[End]
```

---

## UI/UX Architecture

### Design Philosophy

The user interface of ChikitsaLipi is optimized for high legibility, low cognitive load, and ease of use by individuals with varied digital literacy levels in rural or low-resource settings.

1. **Low Cognitive Load**: Minimizes complex multi-level menus, modal distractions, and unnecessary visual ornamentation.
2. **Large Touch Targets**: Primary call-to-action touch areas maintain a minimum height and width of 56dp to 72dp.
3. **Transparent Uncertainty**: The application explicitly highlights extracted fields that have low confidence or missing units rather than hiding potential system errors.
4. **Data Separation**: Clean visual boundaries separate original source text, extracted structured entities, and regional translations.
5. **Dynamic Font & Locale Adaptation**: Supports fluid layout expansion for regional scripts (Bengali and Devanagari) without layout clipping.

### Color Palette Specification

| Purpose | Color Description | Hex Code | Semantic Meaning |
| :--- | :--- | :--- | :--- |
| Primary Surface | Warm Off-White | `#F8F9FA` | Neutral background minimizing visual fatigue |
| Primary Accent | Forest Teal | `#0F5257` | Primary actions, app header, navigation items |
| Secondary Accent | Sage Slate | `#4A7C59` | Secondary buttons, card borders, supporting metadata |
| High Confidence | Emerald | `#059669` | Automated consistency checks passed |
| Uncertainty | Amber | `#D97706` | Field missing unit, low OCR confidence, needs review |
| Critical Warning | Crimson | `#DC2626` | Processing failure, deletion alerts, system errors |

To support users with color vision deficiencies, system state is never communicated solely through color. Every state indicator incorporates text labels, semantic icons, and accessibility content descriptions.

### Typography Hierarchy

| Component | Font Size (sp) | Weight / Style | Primary Application |
| :--- | :--- | :--- | :--- |
| Application Title | 24sp | Bold | Home screen header |
| Screen Title | 24sp | Semi-Bold | Screen headers |
| Section Heading | 18sp | Medium | Card section dividers |
| Body Text | 18sp | Regular | Descriptive text, instructions |
| Medical Values | 18sp | Bold / Monospace | Extracted lab values, vitals |
| Supporting Text | 16sp (min) | Regular | Captions, status metadata |
| Buttons | 16sp - 18sp | Medium / Bold | Touch targets, actions |

Recommended typography engines: Google Sans or Inter for Latin script; Noto Sans Bengali for Bengali script; Noto Sans Devanagari for Hindi script.

---

## Screen-by-Screen Specification

### 1. Home Screen
- **Header**: Displays "ChikitsaLipi", subtitle "Digital Health Record", a tri-language toggle (`English | বাংলা | हिन्दी`), and a total saved record counter (e.g., "12 saved records").
- **Primary Action Card**: Dominant hero card labeled "DIGITIZE NEW HEALTH RECORD" with caption "Photograph a prescription, laboratory report, or clinical note." Triggers Camera Capture Interface.
- **Secondary Action Card**: Labeled "SAVED RECORDS" with caption "View, search, verify, and manage digitized records." Triggers Saved Records Directory.
- **Privacy Notice**: Minimal footer stating: "Your records are processed locally on device and remain under your complete control."

### 2. Camera Capture Interface
- **Viewfinder**: Full-screen live camera preview with dynamic overlay framing guidelines.
- **Real-Time Quality Assessment Banner**: Non-intrusive status top banner displaying:
  - *Good Quality*: "Lighting and focus acceptable"
  - *Low Light*: "Low light detected. Consider using flash or moving to a brighter location."
  - *Blur*: "Image appears blurred. Hold the camera steady."
  - *Document Not Detected*: "Position the complete document inside the frame."
- **Camera Controls**: Bottom bar featuring Flash Toggle (Auto / On / Off), 72dp Shutter Button, and Gallery Import Button.
- **Capture Review**: Post-capture modal offering direct inspection, "Retake", or "Use Image".

### 3. Processing Interface
- **Status Display**: Real-time progress tracker with explicit stage states (`Completed`, `In Progress`, `Pending`, `Failed`).
- **Pipeline Stages**:
  1. Image Quality Assessment
  2. Image Preprocessing
  3. On-Device OCR
  4. Medical Field Extraction
  5. Multilingual Translation
  6. Preparing Verification
- **Cancellation**: "Cancel Processing" button safely aborts operations without saving unverified temporary files.

### 4. Results and Verification Screen
- **Header**: "Review Extracted Record" with back navigation.
- **Dual-Pane View**:
  - *Source Document Pane*: Interactive view with pinch-to-zoom, pan, full-screen viewing, and dynamic bounding box highlight corresponding to selected extracted fields.
  - *Structured Field Cards*: Display category, field name, value, unit, status tag, and regional translations (Bengali/Hindi).
- **Audio Output**: Dedicated "Listen" button triggering Text-to-Speech narration of translated values in the selected language.

### 5. Manual Correction Interface (Bottom Sheet / Full Screen Editor)
- **Source Crop**: Displays zoomed image region corresponding to the targeted field.
- **Field Name & Value Inputs**: Editable text field for name; numeric field backed by a custom medical keypad.
- **Custom Keypad**: Features digits 0–9, decimal point (`.`), slash (`/`), backspace, and clear.
- **Unit Selector**: Chip selection for standard medical units (`g/dL`, `mmHg`, `mg/dL`, `bpm`, `%`, `°C`, `kg`, `cm`, `mL`, `Custom`).
- **Confirmation Action**: Updates status to **User Verified**.

### 6. Saved Records Directory & Record Detail
- **Directory**: Includes search by date, field name, record type, or clinic name; filter chips (`All`, `Vitals`, `Laboratory`, `Prescriptions`, `Other`); and summary cards displaying record metadata.
- **Record Detail**: Full audit view preserving original captured image, raw OCR text, extracted structured fields, user corrections, timestamp, and translations.
- **Export & Delete**: Export options (JSON, Plain Text, PDF) with explicit user confirmation; secure deletion prompt requiring two-step confirmation.

---

## Primary and Secondary Objectives

### Primary Objectives
1. Develop an accessible Android application for digitizing physical health records in low-resource settings.
2. Extract text accurately from photographed printed and handwritten clinical artifacts using on-device OCR.
3. Automatically identify and structure common clinical parameters (vitals, lab results, units).
4. Translate extracted structured text into Bengali and Hindi while ensuring numerical and unit invariance.
5. Provide a dual-pane verification interface allowing users to cross-examine OCR outputs against original document regions.
6. Enable secure, encrypted, on-device storage without requiring continuous cloud connectivity.

### Secondary Objectives
1. Quantify OCR Character Error Rate (CER) and Word Error Rate (WER) across document types.
2. Evaluate medical field, value, and unit extraction accuracy.
3. Characterize pipeline execution speed and latency across varied Android hardware specifications.
4. Evaluate quality and adequacy of Bengali and Hindi translations.
5. Compare performance metrics between printed text and handwritten records.

---

## Primary Endpoints

The system performance is evaluated using the following mathematical formulas:

### 1. Character Error Rate (CER)
Measures string distance at character level between OCR output and ground truth.

$$CER = \frac{S_c + D_c + I_c}{N_c}$$

Where:
- $S_c$ = Number of character substitutions
- $D_c$ = Number of character deletions
- $I_c$ = Number of character insertions
- $N_c$ = Total number of characters in ground truth reference

### 2. Word Error Rate (WER)
Measures distance at word level.

$$WER = \frac{S_w + D_w + I_w}{N_w}$$

Where:
- $S_w$ = Number of word substitutions
- $D_w$ = Number of word deletions
- $I_w$ = Number of word insertions
- $N_w$ = Total number of words in ground truth reference

### 3. Medical Field Extraction Accuracy ($A_{field}$)

$$A_{field} = \frac{C_{field}}{T_{field}} \times 100\%$$

Where $C_{field}$ is the count of correctly identified medical field names and $T_{field}$ is the total number of ground truth medical fields.

### 4. Numerical Value Extraction Accuracy ($A_{value}$)

$$A_{value} = \frac{C_{value}}{T_{value}} \times 100\%$$

Where $C_{value}$ is the count of correctly extracted numeric values matching ground truth exactly.

### 5. Unit Recognition Accuracy ($A_{unit}$)

$$A_{unit} = \frac{C_{unit}}{T_{unit}} \times 100\%$$

Where $C_{unit}$ is the count of correctly recognized and normalized medical measurement units.

### 6. Successful Document Processing Rate ($R_{proc}$)

$$R_{proc} = \frac{N_{successful}}{N_{total\_attempts}} \times 100\%$$

Where $N_{successful}$ represents documents processed through the full pipeline without fatal unhandled exceptions.

---

## Secondary Endpoints

- **Translation Quality Score**: Human-evaluated adequacy and fluency score on a 1–5 Likert scale for Bengali and Hindi renderings.
- **Pipeline Execution Latency**: Time measured in milliseconds from image capture to results display.
- **User Correction Rate**: Proportion of extracted fields modified by human operators during verification.
- **Image Quality Failure Rate**: Percentage of captures flagged as unusable due to severe blur, extreme low light, or severe skew.
- **Printed vs. Handwritten Accuracy Delta**: Comparative CER/WER performance gap between printed records and cursive handwritten notes.
- **System Resource Footprint**: Average CPU utilization, peak RAM consumption (MB), and storage footprint per record.

---

## Evaluation Framework

The evaluation methodology uses a structured testing benchmark:

1. **Dataset Construction**: A standardized benchmark set comprising 300 physical medical documents divided equally into:
   - Printed Laboratory Reports ($n = 100$)
   - Printed Prescriptions ($n = 100$)
   - Handwritten Clinical Notes ($n = 100$)
2. **Environmental Conditions**: Controlled testing under varied illumination levels (100 lux, 300 lux, 800 lux), intentional camera tilt angles ($0^\circ, 15^\circ, 30^\circ$), and simulated motion blur.
3. **Hardware Heterogeneity**: Evaluated across three device tiers: Entry-level (2GB RAM), Mid-range (4GB RAM), and Flagship (8GB+ RAM).
4. **Ground Truth Annotation**: Dual independent expert human annotation of all text tokens, medical entities, values, units, and translations.

---

## Repository Structure

```
ChikitsaLipi/
├── README.md
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/
│       │       └── org/
│       │           └── chikitsalipi/
│       │               ├── camera/          # CameraX management & quality assessment
│       │               ├── extraction/      # Medical field & pattern extraction
│       │               ├── model/           # Data entities, enums, & domain objects
│       │               ├── ocr/             # ML Kit text recognition wrappers
│       │               ├── preprocessing/   # Open-CV / Bitmap image filters & crop
│       │               ├── storage/         # Room Database & Keystore security
│       │               ├── translation/     # Multilingual translation handlers
│       │               ├── ui/              # Jetpack Compose UI screens & theme
│       │               ├── utils/           # Helper utilities & logging
│       │               └── verification/    # Human verification logic & rules
│       └── test/                            # Unit tests
```

---

## Installation

### Prerequisites
- Android Studio Jellyfish (2023.3.1) or newer
- Android SDK 34 (Target SDK 34, Minimum SDK 26)
- JDK 17
- Gradle 8.4+

### Build Instructions

1. Clone repository:
   ```bash
   git clone https://github.com/chikitsalipi/chikitsalipi-android.git
   cd chikitsalipi-android
   ```
2. Open project in Android Studio.
3. Synchronize Gradle project dependencies:
   ```bash
   ./gradlew build
   ```
4. Assemble debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
5. Install on connected physical device:
   ```bash
   ./gradlew installDebug
   ```

---

## Testing Strategy

The repository includes a multi-tiered test suite:

- **Unit Tests**: Test regular expressions, entity parser logic, unit normalizers, and data model mappings.
  ```bash
  ./gradlew test
  ```
- **UI Tests**: Test Compose screen renders, button touch target compliance, and accessibility node hierarchy.
  ```bash
  ./gradlew connectedAndroidTest
  ```
- **Preprocessing Tests**: Test image cropping algorithms, rotation correction, and binarization filters against baseline benchmark images.

---

## Privacy and Ethics

1. **Data Minimization**: ChikitsaLipi extracts and retains only information necessary for digital preservation and medical record translation.
2. **On-Device Confidentiality**: Extracted data and images are persisted locally in sandboxed encrypted storage. No data is transmitted to external servers without explicit user invocation.
3. **Traceability and Accountability**: Automatic OCR field extractions are explicitly distinguished from human-verified fields.
4. **Non-Diagnostic Disclaimer**: ChikitsaLipi is explicitly designated as a digitization tool and research platform. It does not provide autonomous diagnostic recommendations or clinical advice.

---

## Limitations

- **Handwritten Script Variability**: Recognition accuracy for unconstrained cursive handwriting remains significantly lower than for printed text.
- **Complex Tabular Formatting**: Multi-column laboratory reports with non-standard alignment may cause line segmentation errors during text extraction.
- **Non-Standard Medical Abbreviations**: Highly idiosyncratic clinical shorthand may fail automated dictionary lookup.
- **Hardware Limitations**: Real-time camera quality feedback performance depends on hardware-accelerated image analysis support on entry-level devices.

---

## Future Work

1. Incorporation of offline deep neural networks fine-tuned specifically on regional medical handwriting.
2. Support for additional South Asian regional languages (e.g., Tamil, Telugu, Marathi, Odia).
3. Fast Healthcare Interoperability Resources (FHIR) JSON schema export for integration with hospital electronic health record (EHR) systems.
4. Advanced layout-aware document layout analysis (DLA) using vision transformers.
5. On-device neural machine translation model integration for full offline translation capability.

---

## Scientific Software Statement

ChikitsaLipi is an open-source scientific software repository intended for biomedical informatics research, public health record preservation, and accessibility studies. It provides an extensible, reproducible reference framework for studying offline medical text digitization and multilingual extraction.

---

## License

This project is licensed under the Apache License, Version 2.0.
