package com.example.addressbook_lab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Listener listener;
    private List<Contact> contactList;



    DataAdapter(@NonNull List<Contact> contactList, @NonNull Listener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }


    /**
     * интерфейс для доступа в обработчикам событий
     */
    interface Listener {
        void onContactClicked(@NonNull Contact contact);
        void onEdButtonClicked(@NonNull Contact contact);
        void onRmButtonClicked(@NonNull Contact contact);
    }


    /**
     * привязываем макет к данным
     * @param parent экземпляр Родительского контейнера, в котором располагается список
     * @param viewType тип передаваемого View
     * @return экземпляр Хранителя Списка.
     */
    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }


    /**
     * заполняем макет данными
     * @param holder экземпляр Хранителя списка
     * @param position позиция элемента в списке
     */
    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.bind(contact);
    }

    /**
     * получаем количество всех контактов
     * @return Общее число контактов
     */
    @Override
    public int getItemCount() {
        return contactList.size();
    }


    /**
     * инициализируем элементы макета для каждого элемента из набора данных
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView firstName, lastName, patronymic;
        private Button b_edit_contact, b_rm_contact;

        ViewHolder(View view) {
            super(view);
            firstName = view.findViewById(R.id.f_first_name);
            lastName = view.findViewById(R.id.f_last_name);
            patronymic = view.findViewById(R.id.f_patronymic);
            b_edit_contact = view.findViewById(R.id.b_edit_contact);
            b_rm_contact = view.findViewById(R.id.b_rm_contact);

        }

        /**
         * привязка каждого элемента списка и заполнение его данными
         * @param contact список контактов
         */
        void bind(@NonNull Contact contact) {
            firstName.setText(contact.getFirstName());
            lastName.setText(contact.getLastName());
            patronymic.setText(contact.getPatronymic());

            //привязываем обработчики событий к интерфейсу для дальнейшей работы с этими элементами из активности
            itemView.setOnClickListener(v -> listener.onContactClicked(contact));
            b_edit_contact.setOnClickListener(btn_ed -> listener.onEdButtonClicked(contact));
            b_rm_contact.setOnClickListener(btn_rm -> listener.onRmButtonClicked(contact));
        }
    }

}
