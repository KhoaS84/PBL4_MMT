# PBL4_MMT
# Xây dựng trò chơi Tìm số Online thông qua mô hình Client – Server

## Giới thiệu dự án

Đây là báo cáo dự án PBL4 (Project-Based Learning 4) thuộc môn học Hệ điều hành và Mạng máy tính, tập trung vào việc xây dựng một trò chơi tìm số trực tuyến. Trò chơi này được thiết kế và phát triển dựa trên kiến trúc Client - Server, một mô hình phổ biến trong phát triển ứng dụng mạng. Mục tiêu của dự án là tạo ra một môi trường tương tác nơi người chơi có thể kết nối, tham gia các trận đấu tìm số và tương tác với hệ thống quản lý tập trung.

Trò chơi tìm số là một trò chơi giải đố cổ điển, đòi hỏi sự nhanh nhẹn và khả năng tập trung của người chơi. Trong phiên bản trực tuyến này, người chơi sẽ cạnh tranh với nhau hoặc với hệ thống để tìm ra các con số theo một quy tắc nhất định trong thời gian giới hạn.

## Cấu trúc hệ thống

Hệ thống được chia thành hai thành phần chính:

* **Client (Ứng dụng phía người dùng):** Là giao diện mà người chơi trực tiếp tương tác. Ứng dụng Client có trách nhiệm hiển thị giao diện trò chơi, gửi yêu cầu của người chơi đến Server và nhận phản hồi từ Server để cập nhật trạng thái trò chơi.
* **Server (Máy chủ):** Là trung tâm xử lý của hệ thống. Server quản lý tất cả các hoạt động của trò chơi, từ việc xác thực người chơi, quản lý phòng chơi, điều phối các trận đấu cho đến xử lý logic trò chơi và cập nhật dữ liệu.

## Chức năng chi tiết

### Phía Client (Giao diện người dùng)

Ứng dụng Client được xây dựng để cung cấp trải nghiệm người dùng liền mạch và trực quan, bao gồm các chức năng sau:

* **Đăng nhập tài khoản:**
    * Cho phép người chơi nhập tên đăng nhập và mật khẩu để truy cập vào hệ thống.
    * Xử lý xác thực thông tin đăng nhập với Server.
    * Hiển thị thông báo lỗi khi đăng nhập thất bại (ví dụ: sai tên đăng nhập/mật khẩu, tài khoản chưa tồn tại).
* **Đăng ký tài khoản mới:**
    * Cung cấp giao diện để người chơi mới tạo tài khoản.
    * Yêu cầu các thông tin cần thiết như tên đăng nhập, mật khẩu, và có thể là email.
    * Kiểm tra tính hợp lệ của thông tin đăng ký (ví dụ: tên đăng nhập không trùng lặp, mật khẩu đủ mạnh).
    * Gửi yêu cầu đăng ký đến Server và thông báo kết quả cho người dùng.
* **Chơi trò chơi tìm số:**
    * Hiển thị bảng số với các ô số được sắp xếp ngẫu nhiên.
    * Cho phép người chơi tương tác bằng cách chạm/nhấp vào các ô số.
    * Cập nhật trạng thái trò chơi theo thời gian thực (ví dụ: thời gian còn lại, số điểm hiện tại, số lượng số đã tìm thấy).
    * Nhận và hiển thị kết quả trận đấu từ Server.
* **Xem thông tin cá nhân:**
    * Hiển thị hồ sơ của người chơi bao gồm tên đăng nhập, điểm số cao nhất, số trận thắng/thua, v.v.
    * Có thể có chức năng chỉnh sửa một số thông tin cá nhân (nếu được phép).
* **Xem thông tin luật và cách chơi:**
    * Cung cấp một phần hướng dẫn chi tiết về luật chơi của trò tìm số.
    * Giải thích các bước tham gia một trận đấu, cách tính điểm, và các quy tắc đặc biệt khác.

### Phía Server (Máy chủ xử lý)

Server đóng vai trò trung tâm điều khiển và quản lý toàn bộ hệ thống trò chơi, bao gồm:

* **Quản lý thông tin người chơi:**
    * Lưu trữ dữ liệu người chơi trong cơ sở dữ liệu (tên đăng nhập, mật khẩu mã hóa, điểm số, lịch sử đấu, v.v.).
    * Thực hiện các thao tác thêm, sửa, xóa, truy vấn thông tin người chơi.
    * Xác thực thông tin đăng nhập và đăng ký từ phía Client.
* **Điều phối người chơi vào các phòng chơi thích hợp:**
    * Quản lý danh sách các phòng chơi hiện có (phòng trống, phòng đang đấu).
    * Cho phép người chơi tạo phòng mới hoặc tham gia vào phòng có sẵn.
    * Đảm bảo mỗi phòng chơi có đủ số lượng người chơi cần thiết để bắt đầu một trận đấu.
    * Sử dụng các thuật toán điều phối để tối ưu hóa trải nghiệm chơi game (ví dụ: ghép cặp người chơi có trình độ tương đương).
* **Điều khiển các trận đấu:**
    * Khởi tạo một trận đấu mới khi đủ người chơi trong phòng.
    * Tạo ra các bảng số ngẫu nhiên cho từng trận đấu.
    * Xử lý logic trò chơi: xác định số tiếp theo cần tìm, kiểm tra tính hợp lệ của thao tác người chơi, tính toán điểm số.
    * Cập nhật trạng thái trận đấu theo thời gian thực và gửi đến tất cả các Client tham gia.
    * Xác định người thắng cuộc và kết thúc trận đấu.
    * Lưu trữ kết quả trận đấu vào cơ sở dữ liệu.

## Hướng phát triển trong tương lai

Dự án có tiềm năng phát triển rộng lớn với các chức năng và tính năng mới để nâng cao trải nghiệm người dùng:

* **Bảng xếp hạng:**
    * Triển khai hệ thống bảng xếp hạng toàn cầu hoặc theo khu vực/cụm máy chủ.
    * Hiển thị top người chơi có điểm số cao nhất hoặc số trận thắng nhiều nhất.
    * Cung cấp bộ lọc để xem bảng xếp hạng theo ngày, tuần, tháng hoặc toàn thời gian.
* **Tính năng tùy chỉnh giao diện:**
    * Cho phép người chơi tùy chỉnh màu sắc chủ đạo của giao diện trò chơi.
    * Cung cấp các gói chủ đề (themes) khác nhau để thay đổi hình ảnh và phong cách tổng thể của trò chơi.
    * Cho phép tùy chỉnh kiểu dáng và kích thước của các ô số, font chữ, hoặc hiệu ứng âm thanh.
    * Mục tiêu là mang đến trải nghiệm cá nhân hóa mạnh mẽ hơn cho từng người chơi.
* **Chế độ chơi đa dạng:**
    * Thêm các chế độ chơi mới như chơi theo đội, chế độ thử thách thời gian, hoặc chế độ sinh tồn.
    * Mở rộng các quy tắc trò chơi để tạo ra sự đa dạng và thử thách mới.
* **Hệ thống bạn bè và giao tiếp:**
    * Cho phép người chơi thêm bạn bè, tạo nhóm và trò chuyện trong game.
    * Tính năng mời bạn bè tham gia trận đấu.

## Thông tin liên hệ

Mọi thắc mắc hoặc góp ý về dự án xin vui lòng liên hệ:

* **Giảng viên hướng dẫn:** ThS. Trần Hồ Thuỷ Tiên
* **Nhóm sinh viên thực hiện:**
    * Lê Hải Khoa (Lớp: 22T\_KHDL, Nhóm: 22N15B)
    * Hồ Nguyễn Thế Vinh (Lớp: 22T\_KHDL, Nhóm: 22N15B)
    * Ngô Xuân Vinh (Lớp: 22T\_KHDL, Nhóm: 22N15B)
