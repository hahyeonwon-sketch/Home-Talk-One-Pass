

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

        let alarm_badgeNone = event.data === "AllRead_ALARM"

        // 헤더 메뉴의 빨간 점 활성화 및 비 활성화
        const badge = document.getElementById('alarm-badge');
        if (badge) {

            if (alarm_badgeNone) {

                badge.style.display = 'none';
            } else {

                if (badge.style.display === 'none') {

                    badge.style.display = 'block';
                }
            }
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