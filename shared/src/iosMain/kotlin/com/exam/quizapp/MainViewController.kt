package com.exam.quizapp

import androidx.compose.runtime.*
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.exam.quizapp.ui.screens.QuizApp
import com.exam.quizapp.db.initQuizDatabase

fun MainViewController(): UIViewController {
    initQuizDatabase()
    return ComposeUIViewController {
        QuizApp(
            onQuizCompleted = {
                // Xử lý khi hoàn thành quiz trên iOS
            }
        )
    }
}
/*
Chúng ta chưa tạo thư mục iosApp hay project Xcode, và nguyên nhân là vì
bắt buộc phải có macOS và phần mềm Xcode thì mới tạo và chạy được phần đó.
Cụ thể:
1.
Phần đã làm xong (ở phía Kotlin):
◦
Chúng ta đã viết xong file  MainViewController.kt trong module :shared.
Đây chính là "cầu nối" bằng code Kotlin để bên iOS gọi vào.
2.
Phần chưa tạo (Project Xcode / thư mục iosApp):
◦
Thư mục iosApp chứa code Swift và file cấu hình Xcode (.xcodeproj / .xcworkspace).
◦
Vì Xcode và iOS Simulator là môi trường độc quyền của macOS,
nên trên máy tính chạy Windows (hoặc trong Android Studio trên Windows),
chúng ta không thể mở Xcode hay biên dịch trực tiếp ứng dụng iOS được.
Hướng giải quyết tiếp theo:
•
Nếu bạn tiếp tục phát triển trên máy tính Windows: Bạn cứ tập trung hoàn thiện tính năng
và chạy trên Android bình thường. Module :shared đã được thiết kế sẵn sàng cho đa nền tảng.
•
Khi nào bạn (hoặc đồng nghiệp) đưa mã nguồn này sang máy Mac có cài Xcode: Bạn chỉ cần
tạo một project Xcode mới (hoặc sử dụng template Compose Multiplatform),
thêm module :shared vào làm dependency, rồi gọi hàm MainViewController() từ phía Swift là xong!
 */