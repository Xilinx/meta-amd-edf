# Kernel features for virtualization and container support:
#   - bpf: eBPF support for modern networking, tracing, and security
#   - bridge: Network bridging for container and VM networking
#   - kubernetes: Container orchestration support (includes docker, IP-VS, hugetlb)
KERNEL_FEATURES:append = " \
    features/bpf/bpf.scc \
    cfg/net/bridge.scc \
    cfg/kubernetes.scc \
"
