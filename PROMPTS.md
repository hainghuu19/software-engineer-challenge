# AI tools — cách sử dụng trong bài Ad Performance Aggregator

Tài liệu này mô tả **quy trình làm việc với AI (Cursor / trợ lý mã hóa)** cho challenge FV-SEC001 và đính kèm **nguyên văn prompt** mà tôi đã làm.

---

## Quy trình tổng quan

1. **Mô tả bài toán đầy đủ ngay từ đầu**  
   Gửi schema CSV, công thức CTR/CPA, yêu cầu streaming (không đọc cả file vào RAM), CLI `--input` / `--output`, xử lý lỗi, và style code mong muốn.

2. **Cho AI bối cảnh project**  
   Cho biết đã có Maven, Java thuần, thư mục `data/` và `output/`, để AI thiết kế package và entry point phù hợp.

3. **Đối chiếu với README challenge**  
   Sau khi có code, dùng prompt hỏi lại AI (hoặc tự so) checklist: aggregate, top 10, memory, tests, README — để biết phần nào còn thiếu trước khi nộp.

4. **Bổ sung hạ tầng nộp bài**  
   Nhờ AI thêm `README.md`, JUnit, `PROMPTS.md`, Dockerfile theo yêu cầu.

Ngoài việc sử dụng Cursor để generate code, có sử dụng thêm ChatGPT để:

- Tinh chỉnh lại prompt ban đầu cho rõ ràng và đầy đủ hơn
- Đảm bảo prompt đã bao phủ đầy đủ các yêu cầu trong đề bài (streaming, performance, CLI,...)
- Cải thiện cách diễn đạt để AI coding assistant hiểu chính xác ý định

---

## Prompt gốc — bài toán Java CLI (nguyên văn)

Dưới đây là nội dung prompt đã nhập khi bắt đầu implement (không chỉnh sửa):

```
Bạn là một senior Java engineer.

Hãy giúp tôi xây dựng một ứng dụng Java chạy bằng command line (CLI) để xử lý một file CSV lớn (~1GB).
Tôi đã khởi tạo project này với folder data/ad_data.csv (folder chứa file data) và output/ (folder để ghi output)

## Yêu cầu bài toán:

Input: file CSV có các cột:

* campaign_id (string)
* date (YYYY-MM-DD)
* impressions (int)
* clicks (int)
* spend (double)
* conversions (int)

## Nhiệm vụ:

1. Aggregate dữ liệu theo campaign_id:

   * total_impressions
   * total_clicks
   * total_spend
   * total_conversions
   * CTR = total_clicks / total_impressions
   * CPA = total_spend / total_conversions (nếu conversions = 0 thì trả về null)

2. Xuất ra 2 file CSV:

   * top10_ctr.csv → top 10 campaign có CTR cao nhất
   * top10_cpa.csv → top 10 campaign có CPA thấp nhất (loại bỏ campaign có conversions = 0)

## Yêu cầu kỹ thuật:

* File đầu vào rất lớn (~1GB), phải tối ưu memory và performance
* KHÔNG được load toàn bộ file vào RAM
* Phải đọc file theo kiểu streaming (BufferedReader)
* Code phải clean, dễ đọc, dễ maintain

## Setup project:

* Dùng Java thuần (KHÔNG dùng Spring Boot hoặc framework nặng)
* Maven project
* Input: data/ad_data.csv
* Output: output/

## Những gì tôi cần bạn làm:

1. Thiết kế cấu trúc project (package, class)
2. Tạo class model CampaignStats
3. Viết logic đọc và parse CSV
4. Viết logic aggregate bằng HashMap
5. Tính CTR và CPA
6. Sort và lấy top 10
7. Ghi kết quả ra file CSV
8. Parse CLI args (--input, --output)
9. Xử lý lỗi (file không tồn tại, dữ liệu lỗi,...)

## Coding style:

* Code rõ ràng, dễ hiểu
* Đặt tên biến có ý nghĩa
* Tránh code thừa
* Có comment khi cần

## Lưu ý quan trọng:

* KHÔNG dùng readAllLines hoặc load toàn bộ file
* Bỏ qua các dòng CSV bị lỗi
* Hạn chế dùng thư viện ngoài (chỉ dùng khi thực sự cần)

Hãy generate code theo từng bước, bắt đầu từ class Main.
```

---

## Prompt — đối chiếu với README (nguyên văn)

```
hãy đối chiếu với file readme xem phần bạn vừa làm có đáp ứng tiêu chí trong file readme hay không?
# FV-SEC001 - Software Engineer Challenge — Ad Performance Aggregator
[... nội dung README challenge được dán kèm ...]
```

---

## Prompt — bổ sung README, tests, PROMPTS (nguyên văn)

```
Hãy bổ sung file README, tests giúp tôi, thêm nữa bổ sung file PROMPTS.md để mô tả cách tôi sử dụng AI tools xử lí bài toán vừa rồi
```

---

## Prompt - bổ sung Dockerfile

```
Hãy bổ sung Dockerfile cho chương trình này, đồng thời update README.md để phù hợp với thay đổi sử dụng Docker
```

