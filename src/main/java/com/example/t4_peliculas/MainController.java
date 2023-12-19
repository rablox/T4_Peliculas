package com.example.t4_peliculas;

import com.example.t4_peliculas.model.Genero;
import com.example.t4_peliculas.model.Pelicula;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable, EventHandler<ActionEvent> {

    @FXML
    private TableView<Pelicula> tablaPeliculas;
    @FXML
    private TableColumn<?, ?> columnaTitulo;

    @FXML
    private CheckBox checkAdulto;

    @FXML
    private TableColumn<?, ?> columnaMedia;

    @FXML
    private TableColumn<?, ?> columnaPopularidad;

    @FXML
    private ComboBox<Genero> comboGeneros;

    @FXML
    private TableColumn<?, ?> columnaFecha;

    @FXML
    private MenuItem menuSimilares;

    @FXML
    private MenuItem menuDetalles;

    @FXML
    private ObservableList<Pelicula> listaObservablePeliculas;
    private ObservableList<Genero> listaObservableGeneros;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaObservablePeliculas = FXCollections.observableArrayList();
        tablaPeliculas.setItems(listaObservablePeliculas);
        listaObservableGeneros = FXCollections.observableArrayList();
        comboGeneros.setItems(listaObservableGeneros);


        columnaTitulo.setCellValueFactory(new PropertyValueFactory<>("original_title"));
        columnaPopularidad.setCellValueFactory(new PropertyValueFactory<>("popularity"));
        columnaMedia.setCellValueFactory(new PropertyValueFactory<>("vote_average"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("release_date"));

        comboGeneros.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filtraPeliculasPorGenero(newValue);
            }
        });

        checkAdulto.setOnAction(event -> {
            Genero generoSeleccionado = comboGeneros.getValue();
            if (generoSeleccionado != null) {
                filtraPeliculasPorGenero(generoSeleccionado);
            }
        });

        cargaPeliculas("https://api.themoviedb.org/3/movie/now_playing?api_key=4ef66e12cddbb8fe9d4fd03ac9632f6e&language=en-US&page=1");
        cargaComboGeneros("https://api.themoviedb.org/3/genre/movie/list?api_key=4ef66e12cddbb8fe9d4fd03ac9632f6e");
        acciones();
    }


    private void acciones() {
        menuDetalles.setOnAction(this);
        menuSimilares.setOnAction(this);
    }

    public void cargaPeliculas(String urlString) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lectura = reader.readLine();
            JSONObject response = new JSONObject(lectura);
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject user = results.getJSONObject(i);
                String original_title = user.getString("original_title");
                String release_date = user.getString("release_date");
                int popularity = user.getInt("popularity");
                int vote_average = user.getInt("vote_average");
                JSONArray genreIdsJson = user.getJSONArray("genre_ids");
                List<Integer> genreIds = new ArrayList<>();
                for (int j = 0; j < genreIdsJson.length(); j++) {
                    genreIds.add(genreIdsJson.getInt(j));
                }
                boolean adult = user.getBoolean("adult");
                String poster_path = "https://image.tmdb.org/t/p/w500/" + user.getString("poster_path");
                String overview = user.getString("overview");
                int id = user.getInt("id");


                listaObservablePeliculas.add(new Pelicula(original_title, release_date, popularity, vote_average, genreIds, adult, poster_path, overview, id));
            }


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void filtraPeliculasPorGenero(Genero generoSeleccionado) {
        ObservableList<Pelicula> peliculasFiltradas = FXCollections.observableArrayList();

        boolean mostrarAdultos = !checkAdulto.isSelected();

        for (Pelicula pelicula : listaObservablePeliculas) {
            boolean cumpleGenero = generoSeleccionado.getName().equals("All") || pelicula.getGenreIds().contains(generoSeleccionado.getId());
            boolean cumpleAdulto = mostrarAdultos || pelicula.isAdult();

            if (cumpleGenero && cumpleAdulto) {
                peliculasFiltradas.add(pelicula);
            }
        }

        tablaPeliculas.setItems(peliculasFiltradas);
    }


    public void cargaComboGeneros(String urlString) {
        try {
            Genero all = new Genero(-1, "All");
            listaObservableGeneros.add(all);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lectura = reader.readLine();
            JSONObject response = new JSONObject(lectura);
            JSONArray results = response.getJSONArray("genres");
            for (int i = 0; i < results.length(); i++) {
                JSONObject user = results.getJSONObject(i);
                int id = user.getInt("id");
                String name = user.getString("name");

                Genero genero = new Genero(id, name);
                listaObservableGeneros.add(genero);
            }

            comboGeneros.setItems(listaObservableGeneros);


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ObservableList<Pelicula> cargarPeliculasSimilares(int peliculaId) throws IOException {
        URL url = new URL("https://api.themoviedb.org/3/movie/" + peliculaId + "/similar?api_key=4ef66e12cddbb8fe9d4fd03ac9632f6e&language=en-US&page=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String lectura = reader.readLine();
        JSONObject response = new JSONObject(lectura);
        JSONArray results = response.getJSONArray("results");
        ObservableList<Pelicula> peliculasSimilares = FXCollections.observableArrayList();

        for (int i = 0; i < results.length(); i++) {
            JSONObject jsonPelicula = results.getJSONObject(i);
            String original_title = jsonPelicula.getString("original_title");
            String release_date = jsonPelicula.optString("release_date", "");
            int popularity = jsonPelicula.optInt("popularity", 0);
            int vote_average = jsonPelicula.optInt("vote_average", 0);
            boolean adult = jsonPelicula.optBoolean("adult", false);
            String poster_path = "https://image.tmdb.org/t/p/w500/" + jsonPelicula.optString("poster_path", "");
            String overview = jsonPelicula.optString("overview", "");
            int id = jsonPelicula.getInt("id");
            Pelicula pelicula = new Pelicula(original_title, release_date, popularity, vote_average, new ArrayList<>(), adult, poster_path, overview, id);
            peliculasSimilares.add(pelicula);
        }
        return peliculasSimilares;
    }


    private void mostrarDialogoPeliculasSimilares(ObservableList<Pelicula> peliculasSimilares) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Películas Similares");

        ListView<Pelicula> listView = new ListView<>();
        listView.setItems(peliculasSimilares);

        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() == menuDetalles) {

        }
        if (actionEvent.getSource() == menuSimilares) {
            Pelicula peliculaSeleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();
            if (peliculaSeleccionada != null) {
                try {
                    ObservableList<Pelicula> peliculasSimilares = cargarPeliculasSimilares(peliculaSeleccionada.getId());
                    mostrarDialogoPeliculasSimilares(peliculasSimilares);
                } catch (Exception e) {
                }
            }
        }

    }
}
