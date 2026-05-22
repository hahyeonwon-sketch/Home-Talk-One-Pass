
// 1. 데이터 불러오기
const alarmStatus = localStorage.getItem('alarmStatus');

// 2. 콘솔창에서 값 확인
console.log("불러온 알람 상태:", alarmStatus); // 결과: "NEW_ALARM" 또는 null (데이터가 없을 때)

// 3. 실전 활용 (알람 조건문 처리)
if (alarmStatus === "NEW_ALARM") {
    document.getElementById('alarm-badge').style.display = 'block';
}


// 헤더는 모든 페이지 공통이므로 '실시간 알림 대기 및 표시' 역할만 담당합니다.
document.addEventListener("DOMContentLoaded", function() {
    // 타임리프 문법을 사용하여 /hometop/api/notifications/connect 주소를 안전하게 동적 자동 생성합니다.
    const sseUrl = /*[[@{/api/notifications/connect}]]*/ '/hometop/api/notifications/connect';

    console.log("SSE 연결 시도 중... 주소:", sseUrl);

    // Spring Boot의 SseEmitter 연결 엔드포인트
    const eventSource = new EventSource(sseUrl);

    // 1. 404 에러가 풀리고 최초 연결(Handshake)에 완벽히 성공했을 때 실행됩니다.
    eventSource.onopen = function() {
        console.log("[성공] 서버와 실시간 SSE 스트리밍 통로가 연결되었습니다!");
    };

    // 2. 백엔드에서 강제로 보낼 'alarm-signal' 이름의 실시간 알림 신호 감지
    eventSource.addEventListener('alarm-signal', function(event) {
        console.log("[실시간 신호 도착] 데이터 내용:", event.data);

        // 헤더 메뉴의 빨간 점 활성화 및 비 활성화
        const badge = document.getElementById('alarm-badge');
        if (badge) {

            if (event.data === "NEW_ALARM") {

                if (badge.style.display === 'none') {
                    badge.style.display = 'block';
                }
            }
            else {

                if (badge.style.display === 'block') {}
                badge.style.display = 'none';
            }

            localStorage.setItem('alarmStatus', event.data);
        }
    });

    // 3. 서버가 꺼지거나 네트워크 타임아웃 등으로 연결 오류가 발생했을 때
    eventSource.onerror = function(error) {
        if (eventSource.readyState === EventSource.CLOSED) {
            console.log("SSE 연결이 완전히 닫혔습니다. 자동으로 재연결을 시도합니다.");
        } else if (eventSource.readyState === EventSource.CONNECTING) {
            console.log("연결이 일시적으로 끊겨 서버와 재연결을 시도하는 중입니다...");
        }
    };
});

// 자바 백엔드 서버(Spring Boot 등)의 API 엔드포인트로 요청을 보냄
// fetch(sseCheckUrl)
//     .then(response => response.text()) // 응답 데이터를 JSON 형태로 변환
//     .then(data => {
//         console.log("자바 백엔드로부터 받은 데이터:", data);
//
//         if (data === "NEW_ALARM") {
//
//             const badge = document.getElementById('alarm-badge');
//             badge.style.display = 'block';
//         } else {
//
//             const badge = document.getElementById('alarm-badge');
//             if (badge) badge.style.display = 'none';
//         }
//
//     })
//     .catch(error => console.error("통신 실패:", error));
