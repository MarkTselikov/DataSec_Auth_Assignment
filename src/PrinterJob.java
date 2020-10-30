public class PrinterJob {
    private int jobNumber;
    private String fileName;
    private String printer;
    public static int jobNumCounter = 0;

    public PrinterJob(String fileName, String printer) {
        this.jobNumber = jobNumCounter;
        this.fileName = fileName;
        this.printer = printer;

        jobNumCounter++;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Job Number: " + jobNumber
                + ", File Name: " + fileName;
    }
}
