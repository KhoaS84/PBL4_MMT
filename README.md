# PBL4_MMT
# Xây dựng trò chơi Tìm số Online thông qua mô hình Client – Server

## Tóm tắt dự án

Dự án này tập trung vào việc xây dựng một trò chơi "Tìm số" trực tuyến (Online Number Puzzle Game) sử dụng mô hình Client – Server. Mục tiêu là tạo ra một trò chơi giải trí, nơi người chơi có thể cạnh tranh với nhau trong môi trường mạng. Hệ thống bao gồm một máy chủ (Server) quản lý các trận đấu, thông tin người chơi, và điều phối các phiên chơi. Các máy khách (Client) sẽ là giao diện tương tác cho người chơi, cho phép họ đăng nhập, đăng ký, tham gia trò chơi và xem thông tin cá nhân.

Trò chơi được thiết kế để mang lại trải nghiệm mượt mà và trực quan, với các chức năng cơ bản như đăng nhập, đăng ký tài khoản, tham gia vào các phòng chơi, và hiển thị luật chơi. Mục tiêu chính là đảm bảo tính ổn định của hệ thống Client-Server, khả năng xử lý đồng thời nhiều người chơi, và tính bảo mật thông tin người dùng.

Xây dựng ứng dụng tìm kiếm số từ 1 -> 100
Theo giao thức kết nối TCP/IP - Ngôn ngữ JAVA

## Mục tiêu

Mục tiêu của dự án là xây dựng thành công một trò chơi "Tìm số" online hoạt động ổn định trên mô hình Client – Server, đáp ứng các chức năng cơ bản như:
* Cung cấp nền tảng để người chơi tham gia trò chơi online.
* Quản lý thông tin người chơi và các phiên chơi.
* Đảm bảo tính tương tác mượt mà giữa Client và Server.

## Các vấn đề cần giải quyết

* Phân chia rõ ràng chức năng và nhiệm vụ giữa Client và Server.
* Xây dựng cơ chế giao tiếp hiệu quả và an toàn giữa Client và Server.
* Thiết kế giao diện người dùng thân thiện, dễ sử dụng.
* Đảm bảo khả năng xử lý đồng thời nhiều kết nối từ Client.
* Quản lý trạng thái trận đấu và dữ liệu người chơi trên Server.

## Giải pháp tổng quan

| Vấn đề | Giải pháp đề xuất |
| :------------------------------------------ | :--------------------------------------------------------- |
| Kiến trúc hệ thống | Mô hình Client – Server |
| Ngôn ngữ lập trình (Client) | JavaScript |
| Ngôn ngữ lập trình (Server) | Node.js |
| Cơ sở dữ liệu | (Không ghi rõ trong tài liệu, giả định cơ sở dữ liệu phù hợp với Node.js như MongoDB/PostgreSQL/MySQL) |
| Giao diện người dùng | Giao diện web được xây dựng bằng HTML/CSS/JavaScript |
| Giao tiếp mạng | WebSocket hoặc Socket.IO để tạo kết nối thời gian thực |
| Quản lý phiên chơi | Logic xử lý trên Server để tạo phòng, quản lý người chơi và trạng thái trận đấu |

## Thành phần chính của hệ thống

### Client

* **Chức năng đăng nhập**: Cho phép người dùng đăng nhập vào hệ thống bằng tài khoản đã đăng ký.
* **Chức năng đăng ký**: Cho phép người dùng tạo tài khoản mới để tham gia trò chơi.
* **Chức năng chơi trò chơi**: Giao diện chính để người chơi tương tác với bảng số, thực hiện các lượt đi và theo dõi tiến trình trận đấu.
* **Chức năng xem thông tin cá nhân**: Hiển thị hồ sơ, điểm số và các thông tin liên quan khác của người chơi.
* **Chức năng xem thông tin luật và cách chơi**: Cung cấp hướng dẫn chi tiết về luật chơi và cách thao tác trong trò chơi.
* **Giao diện LOGOUT**: Cho phép người dùng thoát khỏi hệ thống.

### Server

* **Quản lý thông tin người chơi**: Lưu trữ và quản lý dữ liệu tài khoản người dùng (tên đăng nhập, mật khẩu, điểm số, v.v.).
* **Điều phối người chơi vào các phòng chơi thích hợp**: Xử lý logic tạo phòng, tham gia phòng và ghép cặp người chơi.
* **Điều khiển các trận đấu**: Quản lý trạng thái của từng trận đấu, kiểm tra lượt đi hợp lệ, xác định người thắng/thua, và cập nhật điểm số.

## Kết quả đạt được

Trò chơi "Tìm số" đã hoàn thành được các chức năng chính sau:

### Client

* Chức năng đăng nhập
* Chức năng đăng ký
* Chức năng chơi trò chơi
* Chức năng xem các thông tin cá nhân
* Chức năng xem thông tin luật và cách chơi

### Server

* Quản lí thông tin người chơi
* Điều phối người chơi vào các phòng chơi thích hợp
* Điều khiển các trận đấu

## Hướng phát triển

Để cải thiện và mở rộng trò chơi, các hướng phát triển sau có thể được xem xét:

* **Bảng xếp hạng**: Hiển thị vị trí từng người chơi trong một khu vực (cụm máy chủ) hoặc toàn cầu, tạo động lực cạnh tranh.
* **Tính năng tùy chỉnh**: Cho phép người chơi tùy chỉnh màu sắc hoặc chủ đề giao diện, bảng số, mang lại trải nghiệm cá nhân hóa.
* **Tính năng trò chuyện (Chat)**: Cho phép người chơi giao tiếp với nhau trong game.
* **Hệ thống bạn bè**: Kết nối và mời bạn bè cùng chơi.
* **Chế độ chơi mới**: Thêm các chế độ chơi đa dạng hơn (ví dụ: chơi theo đội, chơi với AI).
* **Cải thiện bảo mật**: Tăng cường các biện pháp bảo mật cho dữ liệu người dùng và giao tiếp mạng.

## Sinh viên thực hiện

* Lê Hải Khoa
* Hồ Nguyễn Thế Vinh
* Ngô Xuân Vinh

## Giảng viên hướng dẫn

* ThS. Trần Hồ Thủy Tiên

## Thời gian thực hiện

Đà Nẵng, 05/01/2025
