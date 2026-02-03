terraform {
  required_providers {
    proxmox = {
      source = "bpg/proxmox"
      version = "0.46.1"
    }
  }
}

provider "proxmox" {
  endpoint = var.pm_api_url

  # bpg 제공자에게 토큰 전달 (ID와 Secret을 합쳐서 전달)
  # 입력받는 ID가 "user@realm!tokenid" 형식이어야 함
  api_token = "${var.pm_api_token_id}=${var.pm_api_token_secret}"

  # SSL 인증서 검증 무시
  insecure = true

  # SSH 에이전트 사용 안 함 (타임아웃 방지)
  ssh {
    agent = false
  }
}
