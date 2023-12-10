package program.page;

class HostPage implements PageStrategy {
    private String host;

    public HostPage(String host) {
        this.host = host;
    }
    @Override
    public String printCurrentPage() {
        return host;
    }
}
