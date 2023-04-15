package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album=new Album(title);
        albums.add(album);
        Artist artist;
        for(Artist artist1:artists){
            if(artist1.getName().equals(artistName)){

                if(artistAlbumMap.containsKey(artist1)){
                    artistAlbumMap.get(artist1).add(album);
                    return album;
                }
            }
        }

        artist=new Artist(artistName);
        artists.add(artist);

        artistAlbumMap.put(artist,new ArrayList<>());
        artistAlbumMap.get(artist).add(album);

        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song=new Song(title,length);
        songs.add(song);


        for(Album album:albums){
            if(album.getTitle().equals(albumName)){

                if (albumSongMap.containsKey(album)) {
                    albumSongMap.get(album).add(song);
                }else {
                    albumSongMap.put(album,new ArrayList<>());
                    albumSongMap.get(album).add(song);
                }
                break;
            }
        }
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        //created a new playlist
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        //create a playlist with a list of songs
        playlistSongMap.put(playlist,new ArrayList<>());

        //adding songs to the current playlist
        for (Song song:songs){
            if(song.getLength() == length){
                playlistSongMap.get(playlist).add(song);
            }
        }

        //assigning current listener to the playlist

        for(User user:users) {
            if (user.getMobile().equals(mobile)) {
                //assigning creator of the playlist
                creatorPlaylistMap.put(user,playlist);
                    playlistListenerMap.put(playlist,new ArrayList<>());
                    playlistListenerMap.get(playlist).add(user);
                break;
            }
        }
        return playlist;

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        //created a new playlist
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        //create a playlist with a list of songs
        playlistSongMap.put(playlist,new ArrayList<>());

        //adding songs to the current playlist
        for (Song song:songs){
            for(String songName:songTitles){
                if(song.getTitle().equals(songName))
                    playlistSongMap.get(playlist).add(song);
            }
        }

        //assigning current listener to the playlist

        for(User user:users) {
            if (user.getMobile().equals(mobile)) {
                //assigning creator of the playlist
                creatorPlaylistMap.put(user,playlist);
                    playlistListenerMap.put(playlist,new ArrayList<>());
                    playlistListenerMap.get(playlist).add(user);

                break;
            }
        }

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        for(Playlist playlist:playlists){
            //searching for the playlist
            if(playlist.getTitle().equals(playlistTitle)){
                //searching for the given user
                for(User user:users){
                    if(user.getMobile().equals(mobile)) {
                        //checking if the user is the creator
                        if(creatorPlaylistMap.containsKey(user)){
                            if(creatorPlaylistMap.get(user) == playlist){
                                return playlist;
                            }
                        }else if(playlistListenerMap.containsKey(playlist)){
                            //checking if the user is already in the listener list
                            if(playlistListenerMap.get(playlist).contains(user)){
                                return playlist;
                            }
                            else {
                                playlistListenerMap.get(playlist).add(user);
                                return playlist;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return null;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
//searching for the song
        for (Song song : songs) {
            if (song.getTitle().equals(songTitle)) {
                for (User user : users) {
                    //searching for given user
                    if (user.getMobile().equals(mobile)) {
                        //checking if user has already liked song
                        if (songLikeMap.containsKey(song)) {
                            if (songLikeMap.get(song).contains(user)) {
                                return song;
                            } else {
                                //liking the song
                                song.setLikes(song.getLikes() + 1);
                                songLikeMap.get(song).add(user);
                                //liking the corressponding artist also
                                for (Artist artist : artistAlbumMap.keySet()) {
                                    for (Album album : albumSongMap.keySet()) {
                                        if (albumSongMap.get(album).contains(song)) {
                                            artist.setLikes(artist.getLikes() + 1);
                                            return song;
                                        }
                                    }
                                }

                            }

                        } else {
                            songLikeMap.put(song, new ArrayList<>());
                            //liking the song
                            song.setLikes(song.getLikes() + 1);
                            songLikeMap.get(song).add(user);
                            //liking the corressponding artist also
                            for (Artist artist : artistAlbumMap.keySet()) {
                                for (Album album : albumSongMap.keySet()) {
                                    if (albumSongMap.get(album).contains(song)) {
                                        artist.setLikes(artist.getLikes() + 1);
                                        return song;
                                    }
                                }
                            }

                        }
                        break;
                    }


                }
                break;
            }

        }
        return null;
    }

    public String mostPopularArtist() {
        int countLikes=Integer.MIN_VALUE;
        String popularArtist="";
        for(Artist artist:artists){
            if(artist.getLikes() > countLikes){
                popularArtist=artist.getName();
                countLikes=artist.getLikes();
            }
        }
        return popularArtist;
    }

    public String mostPopularSong() {
        int countLikes=Integer.MIN_VALUE;
        String popularSong="";
        for(Song song:songs){
            if(song.getLikes() > countLikes){
                popularSong=song.getTitle();
                countLikes=song.getLikes();
            }
        }
        return popularSong;
    }
}
