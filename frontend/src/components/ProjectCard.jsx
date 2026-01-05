import './ProjectCard.css';

const ProjectCard = ({ project, onActivate, taskCount }) => {
  const isActive = project.status === 'ACTIVE';

  return (
    <div className={`project-card ${isActive ? 'active' : 'pending'}`}>
      <div className="project-header">
        <h3>{project.name}</h3>
        <span className={`status-badge ${isActive ? 'active' : 'pending'}`}>
          {project.status}
        </span>
      </div>
      <p className="project-description">{project.description || 'No description'}</p>
      <div className="project-footer">
        <span className="task-count">📋 {taskCount} tasks</span>
        {!isActive && (
          <button onClick={() => onActivate(project.id)} className="btn-activate">
            Activate
          </button>
        )}
      </div>
    </div>
  );
};

export default ProjectCard;
