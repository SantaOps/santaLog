variable "pm_api_url" {
  type = string
}

# 토큰 ID
variable "pm_api_token_id" {
  type      = string
  sensitive = true
}

# 토큰 Secret (UUID)
variable "pm_api_token_secret" {
  type      = string
  sensitive = true
}

variable "ssh_public_key_path" {
  type = string
}

# 템플릿 이름
variable "template_name" {
  type    = string
  default = "ubuntu-2404-cloud-template"
}

# VM 초기 사용자명
variable "ci_user" {
  type        = string
  description = "VM 생성 시 만들 초기 사용자 이름"
  default     = "kosa" # 기본값. 비밀번호는 나중에 설정
}

# 12개 VM의 설계도를 담는 핵심 변수
variable "vms" {
  type = map(object({
    target_node = string
    vmid        = number
    ip          = string
    gateway     = string
    cores       = number
    memory      = number
    disk_size   = string # 예: "20G", "100G"
    user        = optional(string, "kosa")
    password    = optional(string, "kosa1004")    
  }))
}