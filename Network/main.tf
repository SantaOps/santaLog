resource "proxmox_virtual_environment_vm" "k8s_nodes" {
  for_each = var.vms

  name      = each.key
  node_name = each.value.target_node
  vm_id     = each.value.vmid

  # 템플릿 복제 설정
  clone {
    vm_id = 9000      # 템플릿 VM ID
    full  = true      # Full Clone
    
    # [핵심] 템플릿이 실제로 존재하는 노드 이름 (Santa1)
    # 템플릿이 다른 노드에 있어도 이걸 명시하면 API가 알아서 찾습니다.
    node_name = "santa1"
  }

  agent {
    enabled = false
    # enabled = false # 나중에 qemu 등을 설치하면 true로 바꾸기
  }

  # CPU & Memory
  cpu {
    cores = each.value.cores
    type  = "host"
  }

  memory {
    dedicated = each.value.memory
  }

  vga {
    # type = "std"   # 기본값 (호환성 좋음)
    type = "virtio" # 성능이 더 좋음 (리눅스에서 드라이버 자동 인식)
  }

  # [선택사항] 시리얼 콘솔도 '백업용'으로 남겨두고 싶다면 유지
  # (이걸 지운다고 시리얼이 없어지진 않지만, VGA가 메인이 됩니다)
  serial_device {
    device = "socket"
  }

  # Disk Settings (템플릿 디스크 확장)
  # 아래는 기존 local-lvm을 사용하는 방식
#  disk {
#    datastore_id = "local-lvm"
#    interface    = "scsi0"
#    size         = tonumber(trimsuffix(each.value.disk_size, "G"))
#    file_format  = "raw"
#  }
	# NFS서버에 설치하고 실행만 본인 서버에서 하는 세팅
	disk {
    datastore_id = "local-lvm"
    interface    = "scsi0"
    size         = tonumber(trimsuffix(each.value.disk_size, "G"))
    file_format  = "raw"
  }
  
  # Network
  network_device {
    bridge = "vmbr0"
    model  = "virtio"
    # vlan은 스위치에서 하므로 여기서 굳이 할 필요 없다
    # vlan_id = tonumber(split(".", each.value.ip)[2])
  }

  # Cloud-Init (IP & SSH Key)
  initialization {
    # IP 설정
    ip_config {
      ipv4 {
        address = each.value.ip
        gateway = each.value.gateway
      }
    }

    # 사용자 계정 (ubuntu)
    user_account {
      username = var.ci_user
      password = bcrypt(each.value.password) # 암호화
      keys     = [file(var.ssh_public_key_path)]
    }

    dns {
      servers = [ "8.8.8.8", "1.1.1.1" ]
    }
  }

  # 생성 속도 조절 (병렬 처리 시 충돌 방지)
  lifecycle {
    ignore_changes = [
      initialization, # 생성 후 Cloud-Init 변경 무시
      disk[0].size,
    ]
  }
}
