# 이거 수정하시면 돼요

template_name = "ubuntu-2404-cloud-template"
pm_api_url   = "https://127.0.0.1:8006/api2/json"

vms = {

	# ======================================
	# ---------pfSense, HAProxy-------------
	# ======================================

	# pfSesnse는 수동으로 작성
	# 진입점 : 192.168.50.110
	# 각 pfSense의 IP : 192.168.50.111, 192.168.50.112
	
	# HAProxy의 VIP : 192.168.50.115
  "s5-HAProxy1" = {
    target_node = "santa5"
    vmid        = 5116
    ip          = "192.168.50.116/24"
    gateway     = "192.168.50.110" # pfSense VIP로 설정
    cores       = 4
    memory      = 5120
    disk_size   = "20G"
  }
	"s5-HAProxy2" = {
    target_node = "santa5"
    vmid        = 5117
    ip          = "192.168.50.117/24"
    gateway     = "192.168.50.110" # 서버가 아닌 pfSense VIP로 설정
    cores       = 4
    memory      = 5120
    disk_size   = "20G"
  }

  # ======================================
  # --------- kubernetes, redis ---------
  # ======================================
  
	"s1-Master" = {
    target_node = "santa1"
    vmid        = 1121
    ip          = "192.168.60.121/24"
    gateway     = "192.168.60.1"
    cores       = 2
    memory      = 4096
    disk_size   = "20G"
  }
	"s3-Master" = {
    target_node = "santa3"
    vmid        = 3121
    ip          = "192.168.40.121/24"
    gateway     = "192.168.40.1"
    cores       = 2
    memory      = 4096
    disk_size   = "20G"
  }
	"s4-Master" = {
    target_node = "santa4"
    vmid        = 4221
    ip          = "192.168.40.221/24"
    gateway     = "192.168.40.1"
    cores       = 2
    memory      = 4096
    disk_size   = "20G"
  }
	# ----------------------------------
	"s1-Worker" = {
    target_node = "santa1"
    vmid        = 1126
    ip          = "192.168.60.126/24"
    gateway     = "192.168.60.1"
    cores       = 4
    memory      = 10240
    disk_size   = "20G"
  }
	"s2-Worker" = {
    target_node = "santa2"
    vmid        = 2226
    ip          = "192.168.60.226/24"
    gateway     = "192.168.60.1"
    cores       = 4
    memory      = 10240
    disk_size   = "20G"
  }  
	"s3-Worker" = {
    target_node = "santa3"
    vmid        = 3126
    ip          = "192.168.40.126/24"
    gateway     = "192.168.40.1"
    cores       = 4
    memory      = 8192
    disk_size   = "20G"
  }
	"s4-Worker" = {
    target_node = "santa4"
    vmid        = 4226
    ip          = "192.168.40.226/24"
    gateway     = "192.168.40.1"
    cores       = 4
    memory      = 10240
    disk_size   = "20G"
  }
  # -----------------------------------
	"s1-Redis" = {
    target_node = "santa1"
    vmid        = 1131
    ip          = "192.168.60.131/24"
    gateway     = "192.168.60.1"
    cores       = 2
    memory      = 4096
    disk_size   = "20G"
  }
	"s2-Redis" = {
    target_node = "santa2"
    vmid        = 2231
    ip          = "192.168.60.231/24"
    gateway     = "192.168.60.1"
    cores       = 4
    memory      = 4096
    disk_size   = "20G"
  }
	"s3-Redis" = {
    target_node = "santa3"
    vmid        = 3231
    ip          = "192.168.40.131/24"
    gateway     = "192.168.40.1"
    cores       = 2
    memory      = 1024
    disk_size   = "20G"
  }
	"s4-Redis" = {
    target_node = "santa4"
    vmid        = 4231
    ip          = "192.168.40.231/24"
    gateway     = "192.168.40.1"
    cores       = 4
    memory      = 4096
    disk_size   = "20G"
  }
  
	# ======================================
  # ----------- DB , ProxySQL ------------
  # ======================================
	"s1-ProxySQL" = {
    target_node = "santa1"
    vmid        = 1141
    ip          = "192.168.60.141/24"
    gateway     = "192.168.60.1"
    cores       = 1
    memory      = 2048
    disk_size   = "20G"
  }
	"s2-ProxySQL" = {
    target_node = "santa2"
    vmid        = 2141
    ip          = "192.168.60.241/24"
    gateway     = "192.168.60.1"
    cores       = 2
    memory      = 2048
    disk_size   = "20G"
  }
	"s4-ProxySQL" = {
    target_node = "santa4"
    vmid        = 4141
    ip          = "192.168.40.241/24"
    gateway     = "192.168.40.1"
    cores       = 2
    memory      = 2048
    disk_size   = "20G"
  }
  
	"s1-DB" = {
    target_node = "santa1"
    vmid        = 1242
    ip          = "192.168.60.142/24"
    gateway     = "192.168.60.1"
    cores       = 4
    memory      = 7168
    disk_size   = "20G"
  }
	"s2-DB" = {
    target_node = "santa2"
    vmid        = 2242
    ip          = "192.168.60.242/24"
    gateway     = "192.168.60.1"
    cores       = 4
    memory      = 8192
    disk_size   = "20G"
  }
	"s3-DB" = {
    target_node = "santa3"
    vmid        = 3142
    ip          = "192.168.40.142/24"
    gateway     = "192.168.40.1"
    cores       = 4
    memory      = 8192
    disk_size   = "20G"
  }

  
 	# ======================================
  # ------------ Monitoring --------------
  # ======================================
  
 	"s5-Alert1" = {
    target_node = "santa5"
    vmid        = 5151
    ip          = "192.168.50.151/24"
    gateway     = "192.168.50.110"
    cores       = 1
    memory      = 1024
    disk_size   = "20G"
  }  
	"s5-Alert2" = {
    target_node = "santa5"
    vmid        = 5152
    ip          = "192.168.50.152/24"
    gateway     = "192.168.50.110"
    cores       = 1
    memory      = 1024
    disk_size   = "20G"
  }
 	"s3-Monitor" = {
    target_node = "santa3"
    vmid        = 3152
    ip          = "192.168.40.151/24"
    gateway     = "192.168.40.1"
    cores       = 1
    memory      = 6144
    disk_size   = "20G"
  }
 	"s4-Monitor" = {
    target_node = "santa4"
    vmid        = 4251
    ip          = "192.168.40.251/24"
    gateway     = "192.168.40.1"
    cores       = 2
    memory      = 6144
    disk_size   = "20G"
  }
}