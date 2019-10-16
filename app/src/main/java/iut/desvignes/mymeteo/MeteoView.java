package iut.desvignes.mymeteo;



public interface MeteoView {
    void showMessage(int messageId);

    void notifyItemDeleted();

    int getIconId(String icon);

    void launchMap(MeteoRoom town, String[] arrayName, String[] arrayIcon, double[] arrayLat, double[] arrayLng);

    void setPrefTown(MeteoRoom town);
}
